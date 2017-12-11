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
import com.dev.bruno.ceps.dao.LogradouroDAO;
import com.dev.bruno.ceps.dao.TipoLogradouroDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.services.LogradouroService;

public class LogradouroServiceTest {

	private static LogradouroService service;

	private static LocalidadeDAO cepLocalidadeDAO;

	@BeforeClass
	public static void SetUpTest() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		cepLocalidadeDAO = mock(LocalidadeDAO.class);

		service = new LogradouroService(cepLocalidadeDAO, mock(BairroDAO.class), mock(TipoLogradouroDAO.class),
				mock(LogradouroDAO.class), validator);
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
			service.add(new Logradouro());
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

	@Test
	public void testAddLocalidade() {
		Localidade cepLocalidade = new Localidade();
		cepLocalidade.setNome("Rio de Janeiro");
		cepLocalidade.setId(1L);

		Logradouro cepLogradouro = new Logradouro();
		cepLogradouro.setLocalidade(cepLocalidade);

		when(cepLocalidadeDAO.get(1L)).thenReturn(cepLocalidade);

		try {
			service.add(cepLogradouro);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}

	@Test
	public void testAdd() {
		Localidade cepLocalidade = new Localidade();
		cepLocalidade.setNome("Rio de Janeiro");
		cepLocalidade.setId(1L);

		Logradouro cepLogradouro = new Logradouro();
		cepLogradouro.setLocalidade(cepLocalidade);
		cepLogradouro.setNome("Rua Cidade de Lima");

		when(cepLocalidadeDAO.get(1L)).thenReturn(cepLocalidade);

		Object result = service.add(cepLogradouro);

		assertNotNull(result);

		assertTrue(result instanceof Logradouro);
	}
}