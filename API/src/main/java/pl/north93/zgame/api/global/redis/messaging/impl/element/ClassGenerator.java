package pl.north93.zgame.api.global.redis.messaging.impl.element;

import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_SUPER;
import static jdk.internal.org.objectweb.asm.Opcodes.ACONST_NULL;
import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ARETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.V1_8;


import java.lang.reflect.Field;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import pl.north93.zgame.api.global.redis.messaging.Template;

class ClassGenerator // TODO
{
    public static final ClassGenerator INSTANCE = new ClassGenerator();
    private static final String TEMPLATE_ELEMENT_CLASS = MethodHandleTemplateElement.class.getName().replace('.', '/');

    public ITemplateElement getTemplateElement(final Class<?> clazz, final Field field, final Template template)
    {
        return null;
    }

    private String genClassName(final Class<?> clazz)
    {
        return TEMPLATE_ELEMENT_CLASS + "$" + clazz.getSimpleName();
    }

    public Class<?> generateTemplateElementClass(final Class<?> clazz, final Field field)
    {
        final ClassWriter cw = new ClassWriter(0);

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, this.genClassName(clazz), null, TEMPLATE_ELEMENT_CLASS, null);

        {
            final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, TEMPLATE_ELEMENT_CLASS, "<init>", "(Ljava/lang/reflect/Field;Lpl/north93/zgame/api/global/redis/messaging/Template;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lpl/north93/zgame/api/global/redis/messaging/impl/TestImpl;", null, l0, l2, 0);
            mv.visitLocalVariable("field", "Ljava/lang/reflect/Field;", null, l0, l2, 1);
            mv.visitLocalVariable("template", "Lpl/north93/zgame/api/global/redis/messaging/Template;", null, l0, l2, 2);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

        {
            final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(17, l0);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lpl/north93/zgame/api/global/redis/messaging/impl/TestImpl;", null, l0, l1, 0);
            mv.visitLocalVariable("instance", "Ljava/lang/Object;", null, l0, l1, 1);
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }

        {
            final MethodVisitor mv =  cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(24, l0);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lpl/north93/zgame/api/global/redis/messaging/impl/TestImpl;", null, l0, l1, 0);
            mv.visitLocalVariable("instance", "Ljava/lang/Object;", null, l0, l1, 1);
            mv.visitLocalVariable("value", "Ljava/lang/Object;", null, l0, l1, 2);
            mv.visitMaxs(0, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return null;
    }
}
