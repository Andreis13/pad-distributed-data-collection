
package faf.pad2.validation;

import faf.pad2.common.Employee;
import faf.pad2.common.XmlCollection;
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andrew
 */
public class Main {
    public static class MySchemaOutputResolver extends SchemaOutputResolver {

        @Override
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File file = new File(suggestedFileName);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }

    }
    
    public static class EmployeeXmlCollection extends XmlCollection<Employee> {
        
    }

    public static void main(String[] args) throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(EmployeeXmlCollection.class);
        SchemaOutputResolver sor = new MySchemaOutputResolver();
        jaxbContext.generateSchema(sor);
    }
}
