package pl.asie.lib.util;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class ClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		return arg2;
	}
	
	public static boolean isObfuscated(String name) {
		return !name.contains("."); // HACK, but it works.
	}
	
	public static void setFinal(Field field, Object owner, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(owner, newValue);
	}
	   
	public static void listMethods(ClassNode cn) {
		System.out.println("Mapping bug detected! Listing methods: ");
		for(MethodNode methodd: cn.methods)
			System.out.println("- " + methodd.name + methodd.desc);
	}
	
	public static LabelNode newLabelNode() {
		Label l = new Label();
		LabelNode ln = new LabelNode(l);
		l.info = ln;
		return ln;
	}
	
	public static ClassNode getClassNode(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}
	
	public static ClassNode getClassNode(String name) {
		try {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(name);
			classReader.accept(classNode, 0);
			return classNode;
		} catch(Exception e) { e.printStackTrace(); return null; }
	}
	
	
	public static byte[] writeBytecode(ClassNode node) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		return writer.toByteArray();
	}
	
	public static byte[] writeBytecodeFrames(ClassNode node) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}
	
	public static String getName(String className, String deobfName, String obfName) {
		return (isObfuscated(className) ? obfName : deobfName);
	}
	
	public static MethodNode getMethod(ClassNode node, String mName, String mSig) {
		Iterator<MethodNode> methods = node.methods.iterator();
		while(methods.hasNext()) {
			MethodNode m = methods.next();
			if (m.name.equals(mName) && m.desc.equals(mSig)) {
				return m;
			}
		}
		return null;
	}
	public static FieldNode getField(ClassNode node, String mName, String mSig) {
		Iterator<FieldNode> fields = node.fields.iterator();
		while(fields.hasNext()) {
			FieldNode f = fields.next();
			if (f.name.equals(mName) && f.desc.equals(mSig)) {
				return f;
			}
		}
		return null;
	}
}
