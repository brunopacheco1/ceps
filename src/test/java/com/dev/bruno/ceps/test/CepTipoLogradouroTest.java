package com.dev.bruno.ceps.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dev.bruno.ceps.dao.CepTipoLogradouroDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.CepTipoLogradouro;
import com.dev.bruno.ceps.services.CepTipoLogradouroService;

public class CepTipoLogradouroTest {

	private static CepTipoLogradouroService service;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		service = new CepTipoLogradouroService(mock(CepTipoLogradouroDAO.class), validator);
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
			service.add(new CepTipoLogradouro());
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAdd() {
		CepTipoLogradouro cepTipoLogradouro = new CepTipoLogradouro();
		cepTipoLogradouro.setNome("Rua");

		Object result = service.add(cepTipoLogradouro);

		assertNotNull(result);

		assertTrue(result instanceof CepTipoLogradouro);
	}
}