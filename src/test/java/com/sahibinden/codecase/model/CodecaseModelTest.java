package com.sahibinden.codecase.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }
}
