package com.dev.bruno.ceps.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepTipo;
import com.dev.bruno.ceps.services.CepService;

public class CepTest {

	private static CepService service;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		service = new CepService(mock(CepDAO.class), mock(CepLocalidadeDAO.class), mock(CepBairroDAO.class),
				mock(CepLogradouroDAO.class), validator);
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
			service.add(new Cep());
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAddPadraoCEP() {
		Cep cep = new Cep();
		cep.setCep("20");

		try {
			service.add(cep);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}

		cep.setCep("20101111");

		try {
			service.add(cep);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}

		cep.setCep("20101-111");

		try {
			service.add(cep);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAdd() {
		Cep cep = new Cep();
		cep.setCep("20101-111");
		cep.setTipoCep(CepTipo.UOP);

		Object result = service.add(cep);

		assertNotNull(result);

		assertTrue(result instanceof Cep);
	}
}