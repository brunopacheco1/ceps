package com.dev.bruno.ceps.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.service.CepUFService;

public class CepUFTest {

	private static CepUFService service;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		service = new CepUFService(mock(CepUFDAO.class), validator);
	}

	@Test
	public void testAddNull() {
		try {
			service.add(null);
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

	@Test
	public void testAddEmptyObject() {
		try {
			service.add(new CepUF());
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAdd() {
		CepUF cepUF = new CepUF();
		cepUF.setUf("RJ");

		Object result = service.add(cepUF);

		assertNotNull(result);

		assertTrue(result instanceof CepUF);
	}
}