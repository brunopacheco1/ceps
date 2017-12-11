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

import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.UFDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.services.LocalidadeService;

public class LocalidadeServiceTest {

	private static UFDAO cepUFDAO;

	private static LocalidadeService service;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		cepUFDAO = mock(UFDAO.class);

		service = new LocalidadeService(mock(LocalidadeDAO.class), cepUFDAO, validator);
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
		when(cepUFDAO.get(null)).thenThrow(MandatoryFieldsException.class);
		
		try {
			service.add(new Localidade());
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

	@Test
	public void testAddUf() {
		UF cepUF = new UF();
		cepUF.setNome("RJ");
		cepUF.setId(1L);

		Localidade cepLocalidade = new Localidade();
		cepLocalidade.setUf(cepUF);

		when(cepUFDAO.get(1L)).thenReturn(cepUF);

		try {
			service.add(cepLocalidade);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}
	
	@Test
	public void testAdd() {
		UF cepUF = new UF();
		cepUF.setNome("RJ");
		cepUF.setId(1L);

		Localidade cepLocalidade = new Localidade();
		cepLocalidade.setUf(cepUF);
		cepLocalidade.setNome("Rio de Janeiro");
		
		when(cepUFDAO.get(1L)).thenReturn(cepUF);

		Object result = service.add(cepLocalidade);
		
		assertNotNull(result);
		
		assertTrue(result instanceof Localidade);
	}
}