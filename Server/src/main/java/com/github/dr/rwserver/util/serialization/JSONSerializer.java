package com.github.dr.rwserver.util.serialization;

import com.github.dr.rwserver.struct.ObjectMap;
import com.github.dr.rwserver.util.log.exp.ParseException.ParseJsonException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class JSONSerializer {
    /**
     * Serializing a data object combined by values which types are Number Bollean Map Collection Array null to Json
     *
     * @param object object which will be serialized
     * @return Json string made from object
     * @throws IllegalArgumentException the node object of the data object whose type is not one of Number Bollean Map Collection Array null
     */

    public static String serialize(Object object) throws IllegalArgumentException {
        if (object == null)
            return "null";
        if (object instanceof String)
            return '\"' + ((String) object).replace("\b", "\\b")
                    .replace("\t", "\\t").replace("\r", "\\r")
                    .replace("\f", "\\f").replace("\n", "\\n") + '\"';
        if (object instanceof Number || object instanceof Boolean)
            return object.toString();

        if (object instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                sb.append(serialize(key)).append(':').append(serialize(value)).append(',');
            }
            int last = sb.length() - 1;
            if (sb.charAt(last) == ',') sb.deleteCharAt(last);
            sb.append('}');
            return sb.toString();
        }
        if (object instanceof ObjectMap) {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            ObjectMap<?,?> map = (ObjectMap<?,?>) object;
            map.each((k,v) -> {
                sb.append(serialize(k)).append(':').append(serialize(v)).append(',');
            });
            int last = sb.length() - 1;
            if (sb.charAt(last) == ',') sb.deleteCharAt(last);
            sb.append('}');
            return sb.toString();
        }

        if (object instanceof Collection) {
            return serialize(((Collection) object).toArray());
        }
        if (object.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            int last = Array.getLength(object) - 1;
            for (int i = 0; i <= last; ++i) {
                Object value = Array.get(object, i);
                sb.append(serialize(value)).append(',');
            }
            last = sb.length() - 1;
            if (sb.charAt(last) == ',') sb.deleteCharAt(last);
            sb.append(']');
            return sb.toString();
        }
        throw new IllegalArgumentException(object.toString());
    }

    /**
     * 将json字符串反序列化为数据对象
     *
     * @param json the json string which will be deserialized
     * @return the data object made from json
     * @throws ParseJsonException thrown when parsing a illegal json text
     */
    public static Object deserialize(String json) throws ParseJsonException {
        return new JSONSerializer(json).nextValue();
    }


    private int position;
    private final char[] buffer;

    private JSONSerializer(String string) {
        this.buffer = string.toCharArray();
        this.position = -1;
    }

    private Object nextValue() throws ParseJsonException {
        try {
            char c = this.nextToken();
            switch (c) {
                case '{':
                    try {
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                        if (nextToken() != '}') {
                            --position;
                            while (true) {
                                String key = nextValue().toString();
                                if (nextToken() != ':') {
                                    throw new ParseJsonException(new String(this.buffer), this.position, "Expected a ':' after a key");
                                }
                                map.put(key, nextValue());
                                switch (nextToken()) {
                                    case ';':
                                    case ',':
                                        if (nextToken() == '}') {
                                            return map;
                                        }
                                        --position;
                                        break;
                                    case '}':
                                        return map;
                                    default:
                                        throw new ParseJsonException(new String(this.buffer), this.position, "Expected a ',' or '}'");
                                }
                            }
                        } else return map;
                    } catch (ArrayIndexOutOfBoundsException ignore) {
                        throw new ParseJsonException(new String(this.buffer), this.position, "Expected a ',' or '}'");
                    }


                case '[':
                    try {
                        ArrayList<Object> list = new ArrayList<Object>();
                        if (nextToken() != ']') {
                            --position;
                            while (true) {
                                if (nextToken() == ',') {
                                    --position;
                                    list.add(null);
                                } else {
                                    --position;
                                    list.add(nextValue());
                                }
                                switch (nextToken()) {
                                    case ',':
                                        if (nextToken() == ']') {
                                            return list;
                                        }
                                        --position;
                                        break;
                                    case ']':
                                        return list;
                                    default:
                                        throw new ParseJsonException(new String(this.buffer), this.position, "Expected a ',' or ']'");
                                }
                            }
                        } else return list;
                    } catch (ArrayIndexOutOfBoundsException ignore) {
                        throw new ParseJsonException(new String(this.buffer), this.position, "Expected a ',' or ']'");
                    }


                case '"':
                case '\'':
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        char ch = this.buffer[++position];
                        switch (ch) {
                            case '\n':
                            case '\r':
                                throw new ParseJsonException(new String(this.buffer), this.position, "Unterminated string");
                            case '\\':
                                ch = this.buffer[++position];
                                switch (ch) {
                                    case 'b':
                                        sb.append('\b');
                                        break;
                                    case 't':
                                        sb.append('\t');
                                        break;
                                    case 'n':
                                        sb.append('\n');
                                        break;
                                    case 'f':
                                        sb.append('\f');
                                        break;
                                    case 'r':
                                        sb.append('\r');
                                        break;
                                    case 'u':
                                        int num = 0;
                                        for (int i = 3; i >= 0; --i) {
                                            int tmp = buffer[++position];
                                            if (tmp <= '9' && tmp >= '0')
                                                tmp = tmp - '0';
                                            else if (tmp <= 'F' && tmp >= 'A')
                                                tmp = tmp - ('A' - 10);
                                            else if (tmp <= 'f' && tmp >= 'a')
                                                tmp = tmp - ('a' - 10);
                                            else
                                                throw new ParseJsonException(new String(this.buffer), this.position, "Illegal hex code");
                                            num += tmp << (i * 4);
                                        }
                                        sb.append((char) num);
                                        break;
                                    case '"':
                                    case '\'':
                                    case '\\':
                                    case '/':
                                        sb.append(ch);
                                        break;
                                    default:
                                        throw new ParseJsonException(new String(this.buffer), this.position, "Illegal escape.");
                                }
                                break;
                            default:
                                if (ch == c) {
                                    return sb.toString();
                                }
                                sb.append(ch);
                        }
                    }
            }

            int startPosition = this.position;
            while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0)
                c = this.buffer[++position];
            String substr = new String(buffer, startPosition, position-- - startPosition);
            if (substr.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (substr.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
            if (substr.equalsIgnoreCase("null")) {
                return null;
            }

            char b = "-+".indexOf(substr.charAt(0)) < 0 ? substr.charAt(0) : substr.charAt(1);
            if (b >= '0' && b <= '9') {
                try {
                    Long l = Long.valueOf(substr);
                    if (l.intValue() == l)
                        return l.intValue();
                    return l;
                } catch (NumberFormatException exInt) {
                    try {
                        return Double.parseDouble(substr);
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            return substr;
        } catch (ArrayIndexOutOfBoundsException ignore) {
            throw new ParseJsonException(new String(this.buffer), this.position, "Unexpected end");
        }
    }


    private char nextToken() throws ArrayIndexOutOfBoundsException {
        while (this.buffer[++position] <= ' ') ;
        return this.buffer[position];
    }
}
