package com.dev.bruno.ceps.test;

import static org.junit.Assert.assertTrue;

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.service.CepLocalidadeService;

public class CepLocalidadeTest {

	@Inject
	private CepLocalidadeService service;

	private static EJBContainer container;

	@BeforeClass
	public static void setUp() {
		container = EJBContainer.createEJBContainer();
	}

	@Before
	public void SetUpTest() throws Exception {
		container.getContext().bind("inject", this);
	}

	@AfterClass
	public static void tearDown() {
		container.close();
	}

	@Test
	public void testNullService() {
		assertTrue(service instanceof CepLocalidadeService);
	}

	@Test
	public void testServiceAddNull() {
		try {
			service.add(null);
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

//	@Test
//	public void testServiceAddValidation() {
//		try {
//			service.add(new CepLocalidade());
//		} catch (Exception e) {
//			assertTrue(e instanceof ConstraintViolationException);
//		}
//	}
}