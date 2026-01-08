package com.onepaytaxi.driver.utils;

import android.content.Context;

import com.onepaytaxi.driver.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by developer on 21/2/17.
 */
public class ColorRestore {
    public static Context c;

    /**
     * Adding color files to Local hashmap
     */
    public static synchronized void getAndStoreColorValues(String result, Context c) {
        try {
            ColorRestore.c = c;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("*");

            Systems.out.println("lislength" + nList.getLength());
            int chhh = 0;
            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    chhh++;

                    Element element2 = (Element) node;
                    CL.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
                }
            }
            getColorValueDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting Color values from local hash map
     */
    public static synchronized void getColorValueDetail() {
        Field[] fieldss = R.color.class.getDeclaredFields();
        for (Field field : fieldss) {
            int id = c.getResources().getIdentifier(field.getName(), "color", c.getPackageName());
            if (CL.nfields_byName.containsKey(field.getName())) {
                CL.fields.add(field.getName());
                CL.fields_value.add(c.getResources().getString(id));
                CL.fields_id.put(field.getName(), id);

            }
        }

        for (Map.Entry<String, String> entry : CL.nfields_byName.entrySet()) {
            String h = entry.getKey();
            CL.nfields_byID.put(CL.fields_id.get(h), CL.nfields_byName.get(h));
            // do stuff
        }

    }
}
