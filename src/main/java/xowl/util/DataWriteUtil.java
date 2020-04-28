package xowl.util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author xinxian
 * @create 2020-04-27 10:47
 **/
public class DataWriteUtil {

    public static void writeLong(int valueType, long value, int fieldType, DataOutputStream dataOutputStream) throws IOException {
        //写入字段值类型
        dataOutputStream.writeInt(valueType);
        //写入字段值
        dataOutputStream.writeLong(value);
        //写入字段类型
        dataOutputStream.writeInt(fieldType);
    }

    public static void writeInt(int valueType, int value, int fieldType, DataOutputStream dataOutputStream) throws IOException {
        //写入字段值类型
        dataOutputStream.writeInt(valueType);
        //写入字段值
        dataOutputStream.writeInt(value);
        //写入字段类型
        dataOutputStream.writeInt(fieldType);
    }

    public static void writeDouble(int valueType, double value, int fieldType, DataOutputStream dataOutputStream) throws IOException {
        //写入字段值类型
        dataOutputStream.writeInt(valueType);
        //写入字段值
        dataOutputStream.writeDouble(value);
        //写入字段类型
        dataOutputStream.writeInt(fieldType);
    }

    public static void writeString(int valueType, String value, int fieldType, DataOutputStream dataOutputStream) throws IOException {
        //写入字段值类型
        dataOutputStream.writeInt(valueType);
        //针对string类型的数据需要写入长度
        dataOutputStream.writeInt(value.getBytes().length);
        //写入字段值
        dataOutputStream.write(value.getBytes());
        //写入字段类型
        dataOutputStream.writeInt(fieldType);
    }

}
