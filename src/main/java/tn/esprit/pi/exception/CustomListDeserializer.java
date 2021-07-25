package tn.esprit.pi.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.itextpdf.text.pdf.AcroFields.Item;

public class CustomListDeserializer extends StdDeserializer<List<Item>>{

    public CustomListDeserializer() {
        this(null);
    }

    public CustomListDeserializer(Class<?> vc) {
        super(vc);	
    }

    @Override
    public List<Item> deserialize(
      JsonParser jsonparser, 
      DeserializationContext context) 
      throws IOException, JsonProcessingException {
        
        return new ArrayList<>();
    }
}