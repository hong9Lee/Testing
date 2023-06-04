package demo.testing.v1;

import demo.testing.entity.ReflectionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class ReflectionEntityTest {

    private ReflectionEntity getEntity() {
        ReflectionEntity reflectionEntity = new ReflectionEntity();
        reflectionEntity.setId("1");
        reflectionEntity.setName("Lee");
        reflectionEntity.setAddress(11);
        return reflectionEntity;
    }

    @Test
    @Description("변경된 엔티티 필드만큼 객체를 생성하는 소스")
    void 엔티티변경() throws IllegalAccessException {
        ReflectionEntity originalEntity = getEntity(); // 기존 엔티티
        ReflectionEntity changeEntity = getChangeEntity(); // 변경되어 넘어온 엔티티

        ArrayList<ReflectionEntity> list = new ArrayList<>(); // 변경된 엔티티 리스트
        Field[] fields = originalEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ReflectionEntity reflectionEntityEntity = getSampleEntity(originalEntity); // 기존 엔티티를 새로생성한 엔티티에 복사

            if (!field.get(originalEntity).equals(field.get(changeEntity))) {
                System.out.println("수정된 field name => " + field.getName());
                field.set(reflectionEntityEntity, field.get(changeEntity));
                list.add(reflectionEntityEntity);
            }
        }
    }

    private static ReflectionEntity getSampleEntity(ReflectionEntity originalEntity) {
        ReflectionEntity reflectionEntityEntity = new ReflectionEntity();
        reflectionEntityEntity.setId(originalEntity.getId());
        reflectionEntityEntity.setName(originalEntity.getName());
        reflectionEntityEntity.setAddress(originalEntity.getAddress());
        return reflectionEntityEntity;
    }

    private static ReflectionEntity getChangeEntity() {
        ReflectionEntity changeEntity = new ReflectionEntity();
        changeEntity.setId("1");
        changeEntity.setName("Hong");
        changeEntity.setAddress(3);
        return changeEntity;
    }

    @Test
    void entityTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ReflectionEntity originalEntity = getEntity();

        ReflectionEntity reflectionEntityEntity = new ReflectionEntity();
        reflectionEntityEntity.setId("2");
        reflectionEntityEntity.setName("Lee1");
        reflectionEntityEntity.setAddress(3);

        Method[] methods = ReflectionEntity.class.getMethods();

        Set<String> getters = new HashSet<>();
        Set<String> setters = new HashSet<>();
        for (Method method : methods) {
            if (isGetter(method)) {
                getters.add(method.getName());
            } else if (isSetter(method)) {
                setters.add(method.getName());
            }
        }


        ArrayList<ReflectionEntity> list = new ArrayList<>();
        for (String getter : getters) {
            getSampleEntity(originalEntity);

            Method fieldGetter = ReflectionEntity.class.getMethod(getter);
            Object originalValue = fieldGetter.invoke(originalEntity);
            Object newValue = fieldGetter.invoke(reflectionEntityEntity);

            if (!originalValue.equals(newValue)) {
                String get = getter.replace("get", "");
                for (String setter : setters) {
                    if (get.equals(setter.replace("set", ""))) {

//                        Method fieldSetter = Sample.class.getMethod(setter);
//                        fieldSetter.setAccessible(true);
//                        fieldSetter.invoke(temp, newValue);
//                        list.add(temp);
                    }
                }
            }
        }


        System.out.println();
    }

    public static boolean isGetter(Method method) {
        if (!method.getName().startsWith("get")) return false;
        if (method.getName().equals("getClass")) return false;
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        return true;
    }

    public static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) return false;
        if (method.getParameterTypes().length != 1) return false;
        return true;
    }

}
