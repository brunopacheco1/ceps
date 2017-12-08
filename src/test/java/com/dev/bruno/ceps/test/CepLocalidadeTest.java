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

import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.services.CepLocalidadeService;

public class CepLocalidadeTest {

	private static CepUFDAO cepUFDAO;

	private static CepLocalidadeService service;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		cepUFDAO = mock(CepUFDAO.class);

		service = new CepLocalidadeService(mock(CepLocalidadeDAO.class), cepUFDAO, validator);
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
			service.add(new CepLocalidade());
		} catch (Exception e) {
			assertTrue(e instanceof MandatoryFieldsException);
		}
	}

	@Test
	public void testAddUf() {
		CepUF cepUF = new CepUF();
		cepUF.setUf("RJ");
		cepUF.setId(1L);

		CepLocalidade cepLocalidade = new CepLocalidade();
		cepLocalidade.setCepUF(cepUF);

		when(cepUFDAO.get(1L)).thenReturn(cepUF);

		try {
			service.add(cepLocalidade);
		} catch (Exception e) {
			assertTrue(e instanceof ConstraintViolationException);
		}
	}
	
	@Test
	public void testAdd() {
		CepUF cepUF = new CepUF();
		cepUF.setUf("RJ");
		cepUF.setId(1L);

		CepLocalidade cepLocalidade = new CepLocalidade();
		cepLocalidade.setCepUF(cepUF);
		cepLocalidade.setNome("Rio de Janeiro");
		
		when(cepUFDAO.get(1L)).thenReturn(cepUF);

		Object result = service.add(cepLocalidade);
		
		assertNotNull(result);
		
		assertTrue(result instanceof CepLocalidade);
	}
}