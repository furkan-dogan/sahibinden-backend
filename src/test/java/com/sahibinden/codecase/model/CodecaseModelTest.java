package com.sahibinden.codecase.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CodecaseModelTest {

    private CodecaseModel codecaseModel;

    @BeforeEach
    public void setUp() {
        codecaseModel = new CodecaseModel();
        codecaseModel.setID(1);
        codecaseModel.setTitle("İlan Başlığı");
        codecaseModel.setDescription("İlan Açıklaması");
        codecaseModel.setCategory("Emlak");
        codecaseModel.setStatus("Onay Bekliyor");
    }

    @Test
    public void testGetters() {
        assertEquals(1, codecaseModel.getID());
        assertEquals("İlan Başlığı", codecaseModel.getTitle());
        assertEquals("İlan Açıklaması", codecaseModel.getDescription());
        assertEquals("Emlak", codecaseModel.getCategory());
        assertEquals("Onay Bekliyor", codecaseModel.getStatus());
    }

    @Test
    public void testSetters() {
        codecaseModel.setID(2);
        codecaseModel.setTitle("Yeni Başlık");
        codecaseModel.setDescription("Yeni Açıklama");
        codecaseModel.setCategory("Vasıta");
        codecaseModel.setStatus("Aktif");

        assertEquals(2, codecaseModel.getID());
        assertEquals("Yeni Başlık", codecaseModel.getTitle());
        assertEquals("Yeni Açıklama", codecaseModel.getDescription());
        assertEquals("Vasıta", codecaseModel.getCategory());
        assertEquals("Aktif", codecaseModel.getStatus());
    }

    @Test
    public void testToString() {
        String expectedToString = "CodecaseModel(ID=1, title=İlan Başlığı, description=İlan Açıklaması, category=Emlak, status=Onay Bekliyor)";
        assertEquals(expectedToString, codecaseModel.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        CodecaseModel anotherModel = new CodecaseModel();
        anotherModel.setID(1);
        anotherModel.setTitle("İlan Başlığı");
        anotherModel.setDescription("İlan Açıklaması");
        anotherModel.setCategory("Emlak");
        anotherModel.setStatus("Onay Bekliyor");

        assertEquals(codecaseModel, anotherModel);
        assertEquals(codecaseModel.hashCode(), anotherModel.hashCode());

        anotherModel.setID(2);
        assertNotEquals(codecaseModel, anotherModel);
        assertNotEquals(codecaseModel.hashCode(), anotherModel.hashCode());
    }

    @Test
    public void testAllArgsConstructor() {
        CodecaseModel anotherModel = new CodecaseModel(2, "Başlık", "Açıklama", "Kategori", "Durum");
        assertEquals(2, anotherModel.getID());
        assertEquals("Başlık", anotherModel.getTitle());
        assertEquals("Açıklama", anotherModel.getDescription());
        assertEquals("Kategori", anotherModel.getCategory());
        assertEquals("Durum", anotherModel.getStatus());
    }
}
