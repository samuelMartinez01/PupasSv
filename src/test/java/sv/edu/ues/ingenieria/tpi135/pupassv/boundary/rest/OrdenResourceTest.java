///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
// */
//package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;
//
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.UriInfo;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
//
///**
// *
// * @author samuel
// */
//public class OrdenResourceTest {
//    
//    public OrdenResourceTest() {
//    }
//    
//    @BeforeAll
//    public static void setUpClass() {
//    }
//    
//    @AfterAll
//    public static void tearDownClass() {
//    }
//    
//    @BeforeEach
//    public void setUp() {
//    }
//    
//    @AfterEach
//    public void tearDown() {
//    }
//
//    /**
//     * Test of findRange method, of class OrdenResource.
//     */
//    @Test
//    public void testFindRange() {
//        System.out.println("findRange");
//        int firstResult = 0;
//        int maxResult = 0;
//        OrdenResource instance = new OrdenResource();
//        Response expResult = null;
//        Response result = instance.findRange(firstResult, maxResult);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findById method, of class OrdenResource.
//     */
//    @Test
//    public void testFindById() {
//        System.out.println("findById");
//        Integer id = null;
//        OrdenResource instance = new OrdenResource();
//        Response expResult = null;
//        Response result = instance.findById(id);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of create method, of class OrdenResource.
//     */
//    @Test
//    public void testCreate() {
//        System.out.println("create");
//        Orden tipoSala = null;
//        UriInfo uriInfo = null;
//        OrdenResource instance = new OrdenResource();
//        Response expResult = null;
//        Response result = instance.create(tipoSala, uriInfo);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of update method, of class OrdenResource.
//     */
//    @Test
//    public void testUpdate() {
//        System.out.println("update");
//        Orden tipoSala = null;
//        OrdenResource instance = new OrdenResource();
//        Response expResult = null;
//        Response result = instance.update(tipoSala);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of delete method, of class OrdenResource.
//     */
//    @Test
//    public void testDelete() {
//        System.out.println("delete");
//        Orden tipoSala = null;
//        OrdenResource instance = new OrdenResource();
//        Response expResult = null;
//        Response result = instance.delete(tipoSala);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
//}
