package com.dev.bruno.ceps.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.service.CepBairroService;

public class CepBairroTest {

	private static CepBairroService service;

	private static CepLocalidadeDAO cepLocalidadeDAO;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		cepLocalidadeDAO = mock(CepLocalidadeDAO.class);

		service = new CepBairroService(cepLocalidadeDAO, mock(CepBairroDAO.class), validator);
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
		when(cepLocalidadeDAO.get(null)).thenThrow(MandatoryFieldsException.class);

		try {
			service.add(new CepBairro());
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

	@Test
	public void testAddLocalidade() {
		CepLocalidade cepLocalidade = new CepLocalidade();
		cepLocalidade.setNome("Rio de Janeiro");
		cepLocalidade.setId(1L);

		CepBairro cepBairro = new CepBairro();
		cepBairro.setCepLocalidade(cepLocalidade);

		when(cepLocalidadeDAO.get(1L)).thenReturn(cepLocalidade);

		try {
			service.add(cepBairro);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAdd() {
		CepLocalidade cepLocalidade = new CepLocalidade();
		cepLocalidade.setNome("Rio de Janeiro");
		cepLocalidade.setId(1L);

		CepBairro cepBairro = new CepBairro();
		cepBairro.setCepLocalidade(cepLocalidade);
		cepBairro.setNome("Santo Cristo");

		when(cepLocalidadeDAO.get(1L)).thenReturn(cepLocalidade);

		Object result = service.add(cepBairro);

		assertNotNull(result);

		assertTrue(result instanceof CepBairro);
	}
}