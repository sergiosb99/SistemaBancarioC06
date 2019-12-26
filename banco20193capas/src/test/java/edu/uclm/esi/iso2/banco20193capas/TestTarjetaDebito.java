package edu.uclm.esi.iso2.banco20193capas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.uclm.esi.iso2.banco20193capas.model.Cuenta;
import edu.uclm.esi.iso2.banco20193capas.model.Manager;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ClienteNoAutorizadoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ClienteNoEncontradoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaInvalidaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaSinTitularesException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaYaCreadaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.PinInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.SaldoInsuficienteException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TarjetaBloqueadaException;
import edu.uclm.esi.iso2.banco20193capas.model.Cliente;
import edu.uclm.esi.iso2.banco20193capas.model.Tarjeta;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaDebito;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestTarjetaDebito extends TestCase {
	private Cliente pepe, ana;
	private Cuenta cuentaPepe, cuentaAna;

	@Before
	public void setUp() {
		Manager.getMovimientoDAO().deleteAll();
		Manager.getMovimientoTarjetaCreditoDAO().deleteAll();
		Manager.getTarjetaCreditoDAO().deleteAll();
		Manager.getTarjetaDebitoDAO().deleteAll();
		Manager.getCuentaDAO().deleteAll();
		Manager.getClienteDAO().deleteAll();

		this.pepe = new Cliente("12345X", "Pepe", "Pérez");
		this.pepe.insert();
		this.ana = new Cliente("98765F", "Ana", "López");
		this.ana.insert();
		this.cuentaPepe = new Cuenta(1);
		this.cuentaAna = new Cuenta(2);

	}

	/* TEST COMPRAR POR INTERNET */

	/*
	 * Caso de prueba: Realizar una compra por internet correcta superior a cero Se
	 * prueba a comprar con una tarjeta con un saldo de 1000, un producto que vale 1
	 */

	@Test
	public void testCompraPorInternetCorrectoLimiteInferior() {

		try {

			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);

			TarjetaDebito td = cuentaPepe.emitirTarjetaDebito("12345X");
			int token = td.comprarPorInternet(td.getPin(), 1);
			td.confirmarCompraPorInternet(token);
			assertTrue(cuentaPepe.getSaldo() == 999);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una compra por internet correcta superior a cero Se
	 * prueba a comprar con una tarjeta con un saldo de 1000, un producto que vale 1
	 */

	@Test
	public void testCompraPorInternetCorrectoLimiteSuperior() {

		try {

			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);

			TarjetaDebito td = cuentaPepe.emitirTarjetaDebito("12345X");
			int token = td.comprarPorInternet(td.getPin(), 999);
			td.confirmarCompraPorInternet(token);
			assertTrue(cuentaPepe.getSaldo() == 1);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}

	}

	/*
	 * Caso de prueba: Realizar una compra por internet cuyo importe sea superior al
	 * saldo disponible Se prueba a comprar con una tarjeta con un saldo de 1000, un
	 * producto que vale 1001
	 */
	/*
	 * @Test public void testCompraPorInternetImporteMayorSaldoDisponible() {
	 * 
	 * try { cuentaPepe.addTitular(pepe); cuentaPepe.insert();
	 * cuentaPepe.ingresar(1000);
	 * 
	 * TarjetaDebito td = cuentaPepe.emitirTarjetaDebito("12345X");
	 * td.comprarPorInternet(td.getPin(), 1001);
	 * fail("Esperaba SaldoInsuficienteException"); } catch
	 * (SaldoInsuficienteException e) { } catch (Exception e) {
	 * fail("Excepción inesperada: " + e.getMessage()); } }
	 */

	/*
	 * Caso de prueba: Realizar una compra por internet cuyo importe sea inferior a
	 * cero Se prueba a comprar con una tarjeta con un saldo de 1000, un producto
	 * que vale -1
	 */
	/*
	 * @Test public void testCompraPorInternetImporteMenorCero() { try {
	 * cuentaPepe.addTitular(pepe); cuentaPepe.insert(); cuentaPepe.ingresar(1000);
	 * 
	 * TarjetaDebito td = cuentaPepe.emitirTarjetaDebito("12345X");
	 * td.comprarPorInternet(td.getPin(), -1);
	 * fail("Esperaba ImporteInvalidoException"); } catch (ImporteInvalidoException
	 * e) { } catch (Exception e) { fail("Excepción inesperada: " + e.getMessage());
	 * } }
	 */
	/* TEST COMPRAR */

	/*
	 * Caso de prueba: Realizar una compra correcta con el valor inferior al saldo
	 * disponible Se prueba a comprar con una tarjeta con un saldo de 1000, un
	 * producto que vale 999
	 */
	@Test
	public void testComprarCorrectoValorLimiteSuperior() { // Valor límite inferior
		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);

			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.comprar(tdPepe.getPin(), 999);
			assertTrue(cuentaPepe.getSaldo() == 1);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una compra correcta con el valor superior a cero Se
	 * prueba a comprar con una tarjeta con un saldo de 1000, un producto que vale 1
	 */
	@Test
	public void testComprarCorrectoValorLimiteInferior() { // Valor límite superior
		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);

			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.comprar(tdPepe.getPin(), 1);
			assertTrue(cuentaPepe.getSaldo() == 999);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una compra cuyo importe sea superior al saldo
	 * disponible Se prueba a comprar con una tarjeta con un saldo de 1000, un
	 * producto que vale 1001
	 */

	@Test
	public void testComprarImporteMayorSaldoDisponible() { // Valor límite superior
		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.comprar(tdPepe.getPin(), 1001);
			fail("Esperaba SaldoInsuficienteException");
		} catch (SaldoInsuficienteException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una compra cuyo importe sea inferior a cero Se
	 * prueba a comprar con una tarjeta con un saldo de 1000, un producto que vale
	 * -1
	 */
	@Test
	public void testComprarImporteMenorCero() { // Valor límite inferior
		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.comprar(tdPepe.getPin(), -1);
			fail("Esperaba ImporteInvalidoException");
		} catch (ImporteInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/* TEST SACAR DINERO */

	/*
	 * Caso de prueba: Sacar dinero correctamente con el valor superior a cero Se
	 * prueba a sacar dinero con una tarjeta con un saldo de 1000, un total de 1
	 */
	@Test
	public void testSacarDineroCorrectoLimiteInferior() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.sacarDinero(tdPepe.getPin(), 1);

		} catch (Exception e) {
			fail("ExcepciÃ³n inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Sacar dinero correctamente con el valor inferior al saldo
	 * disponible Se prueba a sacar dinero con una tarjeta con un saldo de 1000, un
	 * total de 999
	 */
	@Test
	public void testSacarDineroCorrectoLimiteSuperior() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.sacarDinero(tdPepe.getPin(), 999);

		} catch (Exception e) {
			fail("ExcepciÃ³n inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Sacar más dinero del saldo disponible Se prueba a sacar
	 * dinero con una tarjeta con un saldo de 1000, un total de 1001
	 */
	@Test
	public void testSacarDineroImporteMayorSaldoDisponible() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.sacarDinero(tdPepe.getPin(), 1001);
			fail("Esperaba SaldoInsuficienteException");
		} catch (SaldoInsuficienteException e) {
		} catch (Exception e) {
			fail("ExcepciÃ³n inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Sacar una cantidad de dinero inferior a cero Se prueba a
	 * sacar dinero con una tarjeta con un saldo de 1000, un total de -1
	 */
	@Test
	public void testSacarDineroImporteMenorCero() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			TarjetaDebito tdPepe = cuentaPepe.emitirTarjetaDebito("12345X");
			tdPepe.sacarDinero(tdPepe.getPin(), -1);
			fail("Esperaba ImporteInvalidoException");
		} catch (ImporteInvalidoException e) {
		} catch (Exception e) {
			fail("ExcepciÃ³n inesperada: " + e.getMessage());
		}
	}

	/* TEST CAMBIAR PIN */

	/*
	 * Caso de prueba: Cambiar el pin viejo por uno nuevo Se prueba a realizar el
	 * cambio del pin de la tarjeta de crédito por uno nuevo
	 */

	@Test
	public void testCambiarPinCorrecto() {

		try {

			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);

			TarjetaDebito td = cuentaPepe.emitirTarjetaDebito("12345X");
			td.cambiarPin(td.getPin(), 6543);

		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Cambiar el pin viejo por uno nuevo con pin incorrecto Se
	 * prueba a realizar el cambio del pin de la tarjeta de crédito por uno nuevo
	 * pero el pin viejo ingresado no es correcto
	 */

	@Test
	public void testCambianPinPinInvalido() {

		try {

			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);

			TarjetaDebito td = cuentaPepe.emitirTarjetaDebito("12345X");
			td.cambiarPin(7342928, 6543);
			fail("Esperaba PinInvalidoException");
		} catch (PinInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}
}
