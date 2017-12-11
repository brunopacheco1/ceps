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

import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.services.BairroService;

public class CepBairroTest {

	private static BairroService service;

	private static LocalidadeDAO cepLocalidadeDAO;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		cepLocalidadeDAO = mock(LocalidadeDAO.class);

		service = new BairroService(cepLocalidadeDAO, mock(BairroDAO.class), validator);
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
			service.add(new Bairro());
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

	@Test
	public void testAddLocalidade() {
		Localidade cepLocalidade = new Localidade();
		cepLocalidade.setNome("Rio de Janeiro");
		cepLocalidade.setId(1L);

		Bairro cepBairro = new Bairro();
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
		Localidade cepLocalidade = new Localidade();
		cepLocalidade.setNome("Rio de Janeiro");
		cepLocalidade.setId(1L);

		Bairro cepBairro = new Bairro();
		cepBairro.setCepLocalidade(cepLocalidade);
		cepBairro.setNome("Santo Cristo");

		when(cepLocalidadeDAO.get(1L)).thenReturn(cepLocalidade);

		Object result = service.add(cepBairro);

		assertNotNull(result);

		assertTrue(result instanceof Bairro);
	}
}