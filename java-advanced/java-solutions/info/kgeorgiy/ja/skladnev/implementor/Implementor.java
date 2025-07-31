package info.kgeorgiy.ja.skladnev.implementor;

import info.kgeorgiy.java.advanced.implementor.Compiler;
import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.tools.JarImpler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

// :NOTE:  info.kgeorgiy.java.advanced.base.ContextException: >>> InterfaceJarImplementorTest interface for info.kgeorgiy.ja.skladnev.implementor.Implementor / === test02_methodlessInterfaces() / Implementing info.kgeorgiy.java.advanced.implementor.full.interfaces.EmptyInterface / java.lang.AssertionError: Error implementing
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.checked(Context.java:79)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.context(Context.java:67)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.context(Context.java:50)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.implement(BaseImplementorTest.java:118)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.test(BaseImplementorTest.java:177)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.testOk(BaseImplementorTest.java:196)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.InterfaceImplementorTest.test02_methodlessInterfaces(InterfaceImplementorTest.java:33)
//    	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
//    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
//    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
//    Caused by: java.lang.AssertionError: Error implementing
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.lambda$implement$0(BaseImplementorTest.java:129)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.lambda$context$0(Context.java:51)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.checked(Context.java:75)
//    	... 9 more
//    Caused by: java.lang.SecurityException: Unable to create temporary file or directory
//    	at java.base/java.nio.file.TempFileHelper.create(TempFileHelper.java:141)
//    	at java.base/java.nio.file.TempFileHelper.createTempDirectory(TempFileHelper.java:171)
//    	at java.base/java.nio.file.Files.createTempDirectory(Files.java:1018)
//    	at info.kgeorgiy.ja.skladnev.implementor.Implementor.createTempDirectory(Implementor.java:208)
//    	at info.kgeorgiy.ja.skladnev.implementor.Implementor.implementJar(Implementor.java:50)
//    	at info.kgeorgiy.java.advanced.implementor.tools/info.kgeorgiy.java.advanced.implementor.tools.InterfaceJarImplementorTest.implementJar(InterfaceJarImplementorTest.java:48)
//    	at info.kgeorgiy.java.advanced.implementor.tools/info.kgeorgiy.java.advanced.implementor.tools.InterfaceJarImplementorTest.implement(InterfaceJarImplementorTest.java:43)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.lambda$implement$0(BaseImplementorTest.java:120)
//    	... 11 more
//    ERROR: Test InterfaceJarImplementorTest.test03_standardInterfaces() failed: >>> InterfaceJarImplementorTest interface for info.kgeorgiy.ja.skladnev.implementor.Implementor / === test03_standardInterfaces() / Implementing info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Accessible / java.lang.AssertionError: Error implementing
//
//    info.kgeorgiy.java.advanced.base.ContextException: >>> InterfaceJarImplementorTest interface for info.kgeorgiy.ja.skladnev.implementor.Implementor / === test03_standardInterfaces() / Implementing info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Accessible / java.lang.AssertionError: Error implementing
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.checked(Context.java:79)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.context(Context.java:67)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.context(Context.java:50)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.implement(BaseImplementorTest.java:118)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.test(BaseImplementorTest.java:177)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.testOk(BaseImplementorTest.java:196)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.InterfaceImplementorTest.test03_standardInterfaces(InterfaceImplementorTest.java:38)
//    	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
//    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
//    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
//    Caused by: java.lang.AssertionError: Error implementing
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.lambda$implement$0(BaseImplementorTest.java:129)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.lambda$context$0(Context.java:51)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.checked(Context.java:75)
//    	... 9 more
//    Caused by: java.lang.SecurityException: Unable to create temporary file or directory
//    	at java.base/java.nio.file.TempFileHelper.create(TempFileHelper.java:141)
//    	at java.base/java.nio.file.TempFileHelper.createTempDirectory(TempFileHelper.java:171)
//    	at java.base/java.nio.file.Files.createTempDirectory(Files.java:1018)
//    	at info.kgeorgiy.ja.skladnev.implementor.Implementor.createTempDirectory(Implementor.java:208)
//    	at info.kgeorgiy.ja.skladnev.implementor.Implementor.implementJar(Implementor.java:50)
//    	at info.kgeorgiy.java.advanced.implementor.tools/info.kgeorgiy.java.advanced.implementor.tools.InterfaceJarImplementorTest.implementJar(InterfaceJarImplementorTest.java:48)
//    	at info.kgeorgiy.java.advanced.implementor.tools/info.kgeorgiy.java.advanced.implementor.tools.InterfaceJarImplementorTest.implement(InterfaceJarImplementorTest.java:43)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.lambda$implement$0(BaseImplementorTest.java:120)
//    	... 11 more
//    ERROR: Test InterfaceJarImplementorTest.test04_extendedInterfaces() failed: >>> InterfaceJarImplementorTest interface for info.kgeorgiy.ja.skladnev.implementor.Implementor / === test04_extendedInterfaces() / Implementing info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Descriptor / java.lang.AssertionError: Error implementing
//
//    info.kgeorgiy.java.advanced.base.ContextException: >>> InterfaceJarImplementorTest interface for info.kgeorgiy.ja.skladnev.implementor.Implementor / === test04_extendedInterfaces() / Implementing info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Descriptor / java.lang.AssertionError: Error implementing
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.checked(Context.java:79)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.context(Context.java:67)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.context(Context.java:50)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.implement(BaseImplementorTest.java:118)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.test(BaseImplementorTest.java:177)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.testOk(BaseImplementorTest.java:196)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.InterfaceImplementorTest.test04_extendedInterfaces(InterfaceImplementorTest.java:43)
//    	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
//    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
//    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
//    Caused by: java.lang.AssertionError: Error implementing
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.lambda$implement$0(BaseImplementorTest.java:129)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.lambda$context$0(Context.java:51)
//    	at info.kgeorgiy.java.advanced.base/info.kgeorgiy.java.advanced.base.Context.checked(Context.java:75)
//    	... 9 more
//    Caused by: java.lang.SecurityException: Unable to create temporary file or directory
//    	at java.base/java.nio.file.TempFileHelper.create(TempFileHelper.java:141)
//    	at java.base/java.nio.file.TempFileHelper.createTempDirectory(TempFileHelper.java:171)
//    	at java.base/java.nio.file.Files.createTempDirectory(Files.java:1018)
//    	at info.kgeorgiy.ja.skladnev.implementor.Implementor.createTempDirectory(Implementor.java:208)
//    	at info.kgeorgiy.ja.skladnev.implementor.Implementor.implementJar(Implementor.java:50)
//    	at info.kgeorgiy.java.advanced.implementor.tools/info.kgeorgiy.java.advanced.implementor.tools.InterfaceJarImplementorTest.implementJar(InterfaceJarImplementorTest.java:48)
//    	at info.kgeorgiy.java.advanced.implementor.tools/info.kgeorgiy.java.advanced.implementor.tools.InterfaceJarImplementorTest.implement(InterfaceJarImplementorTest.java:43)
//    	at info.kgeorgiy.java.advanced.implementor/info.kgeorgiy.java.advanced.implementor.BaseImplementorTest.lambda$implement$0(BaseImplementorTest.java:120)
//    	... 11 more
//    ERROR: Test InterfaceJarImplementorTest.test06_java8I

/**
 * Implementor class for {@link Impler}, {@link JarImpler} interfaces.
 */
public class Implementor implements Impler, JarImpler {
    /**
     * System-dependent line separator.
     */
    private final String NEW_LINE = System.lineSeparator();

    /**
     * TAB space.
     */
    private final String TABULATOR = " ".repeat(4);

    /**
     * Generates .jar file with implementation of provided interface. Creates temp directory to store .java files. If program fails deletes temp directory.
     *
     * @param token   type token to create implementation for.
     * @param jarPath target <var>.jar</var> file.
     * @throws ImplerException if the given interface can not be implemented because of following reasons:
     *                         <ul>
     *                             <li> Implementation error occurs </li>
     *                             <li> Failed compiling provided token </li>
     *                             <li> Failed creating {@code .jar} file </li>
     *                         </ul>
     */
    public void implementJar(Class<?> token, Path jarPath) throws ImplerException {
        Path tempDir = createTempDirectory("impl_temp");

        try {
            implement(token, tempDir);

            Path javaFile = getFullFilePath(token, tempDir, "Impl.java", File.separator);

            compile(token, javaFile);

            Path classFile = getFullFilePath(token, tempDir, "Impl.class", File.separator);

            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

            createJar(token, jarPath, manifest, classFile);
        } finally {
            deleteDirectory(tempDir.toFile());
        }

    }

    /**
     * Generates implementation of provided {@link Class} interface and put it by a specific path.
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws ImplerException if the given interface can not be implemented because of following reasons:
     *                         <ul>
     *                         <li> Provided token is null </li>
     *                         <li> Provided token is not interface </li>
     *                         <li> Provided token is non-public </li>
     *                         <li> Problems with generating implementation if interface </li>
     *                         </ul>
     */
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (!token.isInterface()) {
            throw new ImplerException("Error: only interface is supported.");
        }

        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Error: can not implement non-public interface.");
        }

        Path path = getFullFilePath(token, root, "Impl.java", File.separator);
        createDirectories(path);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bufferedWriter.write(classHeader(token));
            bufferedWriter.write(classMethods(token));
            bufferedWriter.write("}");
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new ImplerException("Error: can not write implementation" + e.getMessage());
        }
    }

    /**
     * Creates .jar file with implementation if provided interface.
     *
     * @param token     the interface token for which the implementation was created
     * @param jarPath   the target JAR file path
     * @param manifest  the manifest describing the JAR metadata
     * @param classFile the compiled implementation {@code .class} file to be included in the JAR
     * @throws ImplerException if an I/O error occurs while creating or writing to the JAR file
     */
    private void createJar(Class<?> token, Path jarPath, Manifest manifest, Path classFile) throws ImplerException {
        try (JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
            String entryName = getFilePath(token, "Impl.class", "/");
            JarEntry entry = new JarEntry(entryName);
            jarOut.putNextEntry(entry);
            Files.copy(classFile, jarOut);
            jarOut.closeEntry();
        } catch (IOException e) {
            throw new ImplerException("Error: failed to create jar" + e.getMessage());
        }
    }

    /**
     * Compiles the specified {@code .java} file using the {@link Compiler} utility by a provided path.
     *
     * @param token    the interface token for which the source file was generated
     * @param javaFile the path to the {@code .java} source file to compile
     * @throws ImplerException if a compilation error or assertion error occurs
     */
    private void compile(Class<?> token, Path javaFile) throws ImplerException {
        try {
            Compiler.compile(java.util.List.of(javaFile), java.util.List.of(token), StandardCharsets.UTF_8);
        } catch (AssertionError e) {
            throw new ImplerException("Error: failed to compile class" + e.getMessage());
        }
    }

    /**
     * Constructs the full file path for the implementation file.
     *
     * @param token     the interface token used to derive the package structure
     * @param path      the base directory
     * @param suffix    the file suffix (e.g., {@code "Impl.java"} or {@code "Impl.class"})
     * @param separator the path separator (e.g., {@code File.separator})
     * @return a {@link Path} representing the full path to the implementation file
     */
    private Path getFullFilePath(Class<?> token, Path path, String suffix, String separator) {
        return path.resolve(getFilePath(token, suffix, separator));
    }

    /**
     * Returns the relative path for the specified interface.
     *
     * @param token     the interface token whose package and simple name are used
     * @param suffix    the file suffix {@code "Impl.java"} or {@code "Impl.class"}
     * @param separator the path separator {@code "/"}
     * @return a {@link String} path
     */
    private String getFilePath(Class<?> token, String suffix, String separator) {
        return token.getPackageName().replace(".", separator) + separator + token.getSimpleName() + suffix;
    }

    /**
     * Creates directory for the given {@code path}.
     *
     * @param path the file path for which parent directories must be created
     * @throws ImplerException if an I/O error occurs while creating directories
     */
    private void createDirectories(Path path) throws ImplerException {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new ImplerException("Error: can not create directories for output" + e.getMessage());
        }
    }

    /**
     * Recursively deletes the specified directory.
     *
     * @param file the directory to delete
     * @throws ImplerException if any file or subdirectory cannot be deleted
     */
    private void deleteDirectory(File file) throws ImplerException {
        for (File subfile : Objects.requireNonNull(file.listFiles())) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            boolean deleted = subfile.delete();
            if (!deleted) {
                throw new ImplerException("Error: can not delete temp dir");
            }
        }
    }

    /**
     * Creates a temporary directory with the specified prefix.
     *
     * @param prefix the prefix for the temporary directory name
     * @return a {@link Path} to the newly created temporary directory
     * @throws ImplerException if an I/O error occurs while creating the temporary directory
     */
    private Path createTempDirectory(String prefix) throws ImplerException {
        try {
            return Files.createTempDirectory(Paths.get("").toAbsolutePath(), prefix);
        } catch (IOException e) {
            throw new ImplerException("Error: can not create temp root" + e.getMessage());
        }
    }

    /**
     * Creates a specific header string for the implementation class
     *
     * @param token the interface token for which the header is being generated
     * @return a {@link String} of header, package declaration of a specific class
     */
    private String classHeader(Class<?> token) {
        StringBuilder header = new StringBuilder();

        header.append("package ")
                .append(token.getPackage().getName())
                .append(";")
                .append(NEW_LINE)
                .append(NEW_LINE)
                .append("public class ")
                .append(token.getSimpleName())
                .append("Impl")
                .append(" implements ")
                .append(token.getCanonicalName())
                .append(" {")
                .append(NEW_LINE);

        return header.toString();
    }

    /**
     * Creates an implementations of all public methods declared in the given interface.
     *
     * @param token the interface token which methods are to be implemented
     * @return a {@link String} of implemented source code
     */
    private String classMethods(Class<?> token) {
        StringBuilder methods = new StringBuilder();

        for (Method method : token.getMethods()) {
            methods.append(classMethod(method));
        }

        return methods.toString();
    }

    /**
     * Creates an implementation for a specific method.
     *
     * @param method the {@link Method} method to be implemented
     * @return a {@link String} of implemented method
     */
    private String classMethod(Method method) {
        StringBuilder methodCode = new StringBuilder();

        int modifiers = method.getModifiers() & ~(Modifier.ABSTRACT | Modifier.TRANSIENT);
        methodCode.append(TABULATOR)
                .append(Modifier.toString(modifiers))
                .append(" ")
                .append(method.getReturnType().getCanonicalName())
                .append(" ")
                .append(method.getName())
                .append("(")
                .append(getParameters(method))
                .append(")")
                .append(getExceptions(method))
                .append(" {")
                .append(NEW_LINE)
                .append(TABULATOR)
                .append(TABULATOR)
                .append("return ")
                .append(getDefaultReturnValue(method.getReturnType()))
                .append(";")
                .append(NEW_LINE)
                .append(TABULATOR)
                .append("}")
                .append(NEW_LINE);

        return methodCode.toString();
    }

    /**
     * Creates parameters for the specified method.
     *
     * @param method the {@link Method} method which parameters are generated
     * @return a {@link String} of parameters separated by comma
     */
    private String getParameters(Method method) {
        StringJoiner parameters = new StringJoiner(", ");
        int index = 0;
        for (Class<?> parameter : method.getParameterTypes()) {
            parameters.add(parameter.getCanonicalName() + " arg" + index++);
        }
        return parameters.toString();
    }

    /**
     * Creates a {@code throws} for the specified method.
     *
     * @param method the {@link Method} method which exceptions are generated
     * @return a {@link String} of throws args or an empty string if there are not any
     */
    private String getExceptions(Method method) {
        StringBuilder exceptionsStr = new StringBuilder();
        Class<?>[] exceptions = method.getExceptionTypes();

        if (exceptions.length != 0) {
            exceptionsStr.append(" throws ");
            StringJoiner exceptionList = new StringJoiner(", ");
            for (Class<?> exception : exceptions) {
                exceptionList.add(exception.getCanonicalName());
            }
            exceptionsStr.append(exceptionList);
        }
        return exceptionsStr.toString();
    }

    /**
     * Returns a default return value for a specific return type:
     * <ul>
     *   <li>{@code void} -> an empty string (no return statement)</li>
     *   <li>{@code boolean} -> {@code false}</li>
     *   <li>other primitive types -> {@code 0}</li>
     *   <li>non-primitive types -> {@code null}</li>
     * </ul>
     *
     * @param returnType the return type of the method
     * @return a {@link String} default value to return of given method
     */
    private String getDefaultReturnValue(Class<?> returnType) {
        if (returnType.equals(void.class)) {
            return "";
        } else if (returnType.isPrimitive()) {
            if (returnType.equals(boolean.class)) {
                return "false";
            }
            return "0";
        }
        return "null";
    }

    /**
     * Main for running the {@code Implementor} from the command line.
     *
     * <p>Usage:
     * <ul>
     *   <li>{@code -jar <interface_name> <path_to_jar>} to generate a JAR containing the interface implementation</li>
     *   <li>{@code <interface_name> <path_to_dir>} to generate a {@code .java} file only</li>
     * </ul>
     *
     * @param args command-line arguments specified by the mode of operation:
     *             <ul>
     *               <li>{@code -jar <interface_name> <path_to_jar>}</li>
     *               <li>{@code <interface_name> <path_to_dir>}</li>
     *             </ul>
     */
    public static void main(String[] args) {
        try {
            if (args.length == 3 && args[0].equals("-jar")) {
                new Implementor().implementJar(Class.forName(args[1]), Path.of(args[2]));
            } else if (args.length == 2) {
                new Implementor().implement(Class.forName(args[0]), Path.of(args[1]));
            } else {
                System.err.println("Usage:");
                System.err.println("java -jar <interface_name> <path_to_jar>  - for generating jar");
                System.err.println("java <interface_name> <path_to_dir>  - for generating .java");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}