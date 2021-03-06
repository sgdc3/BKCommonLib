package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.Bukkit;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * This facility creates an appropriate Server object for use within Bukkit.
 * It should accurately try to detect and create the server BKCommonLib is linked against.
 */
public class TestServerFactory {

    public static void initTestServer() {
        if (CommonServerBase.SERVER_CLASS == null) {
            throw new IllegalStateException("Unable to detect server type during test");
        }

        //System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);

        String cb_root = getPackagePath(CommonServerBase.SERVER_CLASS);
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf('.'));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = getPackagePath(f.getType());
        } catch (Throwable t) {}
        cb_root += ".";
        nms_root += ".";

        //System.out.println("CB ROOT: " + cb_root);
        //System.out.println("NMS ROOT: " + nms_root);

        try {
            // Bootstrap is required
            Class<?> dispenserRegistryClass = Class.forName(nms_root + "DispenserRegistry");
            Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("c");
            dispenserRegistryBootstrapMethod.invoke(null);

            // Create some stuff by null-constructing them (not calling initializer)
            // This prevents loads of extra server logic executing during test
            ClassTemplate<?> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
            Object server = server_t.newInstanceNull();
            Class<?> minecraftServerType = Class.forName(nms_root + "MinecraftServer");
            Class<?> dedicatedType = Class.forName(nms_root + "DedicatedServer");
            ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
            Object mc_server = mc_server_t.newInstanceNull();

            // Create data converter registry manager object - used for serialization/deserialization
            // Only used >= MC 1.10.2
            Class<?> dataConverterRegistryClass = null;
            try {
                dataConverterRegistryClass = Class.forName(nms_root + "DataConverterRegistry");
                Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a");
                Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null);
                setField(mc_server, "dataConverterManager", dataConverterManager);
            } catch (ClassNotFoundException ex) {}

            // Create CraftingManager instance and load recipes for >= MC 1.13
            boolean hasLocalCraftingManager = false;
            try {
                minecraftServerType.getDeclaredMethod("getCraftingManager");
                hasLocalCraftingManager = true;
            } catch (Throwable t) {}
            if (hasLocalCraftingManager) {
                // this.ac = new ResourceManager(EnumResourcePackType.SERVER_DATA);
                {
                    setField(mc_server, "ac", createFromCode(minecraftServerType,
                            "return new ResourceManager(EnumResourcePackType.SERVER_DATA);"));
                }

                // this.resourcePackRepository = new ResourcePackRepository(ResourcePackLoader::new);
                {
                    final FastMethod<Object> loaderCreator = compileCode(minecraftServerType,
                            "public static Object create(Object args_t) {"
                           +"  Object[] args = (Object[]) args_t;"
                           +"  return new ResourcePackLoader("
                           + "     (String) args[0],"
                           + "     ((Boolean) args[1]).booleanValue(), "
                           + "     (java.util.function.Supplier) args[2], "
                           + "     (IResourcePack) args[3], "
                           + "     (ResourcePackInfo) args[4], "
                           + "     (ResourcePackLoader$Position) args[5]"
                           + ");"
                           +"}");

                    Class<?> resourcePackLoaderFuncType = Class.forName(nms_root + "ResourcePackLoader$b");
                    Object resourcePackLoaderFunc = Proxy.newProxyInstance(
                           TestServerFactory.class.getClassLoader(),
                           new Class<?>[]{resourcePackLoaderFuncType},
                           new InvocationHandler() {
                               @Override
                               public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                   return loaderCreator.invoke(null, args);
                               }
                           });

                    Class<?> resourcePackRepositoryType = Class.forName(nms_root + "ResourcePackRepository");
                    setField(mc_server, "resourcePackRepository", construct(resourcePackRepositoryType, resourcePackLoaderFunc));
                }

                // this.resourcePackRepository.a((ResourcePackSource) (new ResourcePackSourceVanilla()));
                {
                    /*
                    compileCode(minecraftServerType,
                            "public void register() {"
                          + "  instance.getResourcePackRepository().a((ResourcePackSource) (new ResourcePackSourceVanilla()));"
                          + "  instance.getResourcePackRepository().a();"
                          + "  instance.getResourcePackRepository().a(new java.util.ArrayList());"
                          + "}").invoke(mc_server);
                          */
                }

                // this.ag = new CraftingManager();
                {
                    Class<?> craftingManagerType = Class.forName(nms_root + "CraftingManager");
                    setField(mc_server, "ag", craftingManagerType.newInstance());
                }

                // this.ah = new TagRegistry();
                {
                    Class<?> craftingManagerType = Class.forName(nms_root + "TagRegistry");
                    setField(mc_server, "ah", craftingManagerType.newInstance());
                }

                // this.ac.a((IResourcePackListener) this.ah);
                {
                    compileCode(minecraftServerType,
                              "public void register() {"
                            + "  instance.getResourceManager().a(instance.getTagRegistry());"
                            + "}").invoke(mc_server);
                }
                
                // this.ac.a((IResourcePackListener) this.ag);
                {
                    compileCode(minecraftServerType,
                              "public void register() {"
                            + "  instance.getResourceManager().a(instance.getCraftingManager());"
                            + "}").invoke(mc_server);
                }

                // Initialize the server further, loading the resource packs, by calling MinecraftServer.a(File, WorldData)
                File serverDir = new File(System.getProperty("user.dir"), "target");
                Class<?> worldDataType = Class.forName(nms_root + "WorldData");
                java.lang.reflect.Constructor<?> con = worldDataType.getDeclaredConstructor();
                con.setAccessible(true);
                Object worldData = con.newInstance();
                java.lang.reflect.Method m = minecraftServerType.getDeclaredMethod("a", File.class, worldDataType);
                m.setAccessible(true);
                m.invoke(mc_server, serverDir, worldData);                
            }

            // Initialize some of the fields so they don't result into NPE during test
            setField(server, "console", mc_server);
            setField(server, "logger",  MountiplexUtil.LOGGER);

            // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
            Field bkServerField = Bukkit.class.getDeclaredField("server");
            bkServerField.setAccessible(true);
            bkServerField.set(null, server);
        } catch (Throwable t) {
            System.err.println("Failed to initialize server under test");
            System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);
            System.out.println("Detected NMS_ROOT: " + nms_root);
            System.out.println("Detected CB_ROOT: " + cb_root);
            t.printStackTrace();
        }
    }

    protected static void setField(Object instance, String name, Object value) {
        Field f = null;
        Class<?> t = instance.getClass();
        while (t != null && f == null) {
            try {
                f = t.getDeclaredField(name);
                f.setAccessible(true);
            } catch (Throwable ex) {}
            t = t.getSuperclass();
        }
        if (f == null) {
            throw new RuntimeException("Field " + name + " not found in " + instance.getClass().getName());
        }
        try {
            f.set(instance, value);
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to set field " + name, ex);
        }
    }

    protected static Object getStaticField(Class<?> type, String name) {
        try {
            java.lang.reflect.Field f = type.getDeclaredField(name);
            f.setAccessible(true);;
            return f.get(null);
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to get field " + name, ex);
        }
    }

    protected static Object createFromCode(Class<?> type, String code) {
        return compileCode(type, "public static Object create() {" + code + "}").invoke(null);
    }

    protected static FastMethod<Object> compileCode(Class<?> type, String code) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(type);
        MethodDeclaration dec = new MethodDeclaration(resolver, code);
        FastMethod<Object> m = new FastMethod<Object>();
        m.init(dec);
        return m;
    }

    protected static Object construct(Class<?> type, Object... parameters) {
        try {
            for (java.lang.reflect.Constructor<?> constructor : type.getDeclaredConstructors()) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length == parameters.length) {
                    boolean suitable = true;
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (parameters[i] == null) {
                            continue;
                        }
                        if (!paramTypes[i].isAssignableFrom(parameters[i].getClass())) {
                            suitable = false;
                            break;
                        }
                    }
                    if (suitable) {
                        constructor.setAccessible(true);
                        return constructor.newInstance(parameters);
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to construct " + type.getSimpleName(), t);
        }
        throw new RuntimeException("Constructor not found in " + type.getSimpleName());
    }

    protected static String getPackagePath(Class<?> type) {
        return type.getPackage().getName();
    }

    public static class ServerHook extends ClassHook<ServerHook> {
        @HookMethod("public String getVersion()")
        public String getVersion() {
            return "DUMMY_SERVER";
        }

        @HookMethod("public String getBukkitVersion()")
        public String getBukkitVersion() {
            return "DUMMY_BUKKIT";
        }
    }
}
