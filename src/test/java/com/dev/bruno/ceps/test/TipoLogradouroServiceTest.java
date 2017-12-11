package com.dev.bruno.ceps.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dev.bruno.ceps.dao.TipoLogradouroDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.TipoLogradouro;
import com.dev.bruno.ceps.services.TipoLogradouroService;

public class TipoLogradouroServiceTest {

	private static TipoLogradouroService service;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		service = new TipoLogradouroService(mock(TipoLogradouroDAO.class), validator);
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
			service.add(new TipoLogradouro());
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAdd() {
		TipoLogradouro cepTipoLogradouro = new TipoLogradouro();
		cepTipoLogradouro.setNome("Rua");

		Object result = service.add(cepTipoLogradouro);

		assertNotNull(result);

		assertTrue(result instanceof TipoLogradouro);
	}
}