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
import edu.uclm.esi.iso2.banco20193capas.model.Cliente;
import edu.uclm.esi.iso2.banco20193capas.model.Tarjeta;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaCredito;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCuenta extends TestCase {

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

	/*
	 * Caso de prueba: Creación valida de un objeto Cuenta Para que sea valido,
	 * tiene que añadirse un titular a la cuenta
	 */
	@Test
	public void testCreacionDeUnaCuenta() {
		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			assertTrue(cuentaPepe.isCreada());
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Creación invalida de un objeto Cuenta Para que sea valido,
	 * tiene que añadirse un titular a la cuenta
	 */
	@Test
	public void testNoCreacionDeUnaCuenta() {

		try {
			this.cuentaPepe.insert();
			fail("Esperaba CuentaSinTitularesException");
		} catch (CuentaSinTitularesException e) {
		}
	}

	/*
	 * Caso de prueba: Ingreso valido en un objeto Cuenta Se ingresa un valor
	 * positivo en una cuenta.
	 */
	@Test
	public void testIngresoImporteValido() {
		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			this.cuentaPepe.ingresar(1000);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}

	/*
	 * Caso de prueba: Ingreso invalido en un objeto Cuenta Se prueba la
	 * circunstacia de ingresar 0 euros, invalida.
	 */
	@Test
	public void testIngresoImporteInvalido() {
		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			this.cuentaPepe.ingresar(0);
			fail("Esperaba ImporteInvalidoException");
		} catch (ImporteInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}

	/*
	 * Caso de prueba: Retirar dinero de una cuenta correctamente Se prueban dos
	 * circunstancias: La primera, que el importe sea menor que el valor de
	 * getSaldo() La segunda, que el importe sea igual que el valor de getSaldo()
	 */
	@Test
	public void testRetiradaConSaldo() {

		testIngresoImporteValido();
		try {
			this.cuentaPepe.retirar(500);
		} catch (ImporteInvalidoException e) {
			fail("Se ha producido ImporteInvalidoException");
		} catch (SaldoInsuficienteException e) {
			fail("Se ha producido SaldoInsuficienteException");
		}

		try {
			this.cuentaPepe.retirar(500);
		} catch (ImporteInvalidoException e) {
			fail("Se ha producido ImporteInvalidoException");
		} catch (SaldoInsuficienteException e) {
			fail("Se ha producido SaldoInsuficienteException");
		}
	}

	/*
	 * Caso de prueba: Retirar dinero de una cuenta incorrectamente Se prueba a
	 * retirar mas dinero de una cuenta que lo ingresado anteriormente.
	 */
	@Test
	public void testRetiradaSinSaldo() {

		testIngresoImporteValido();
		try {
			this.cuentaPepe.retirar(2000);
			fail("Esperaba SaldoInsuficienteException");
		} catch (ImporteInvalidoException e) {
			fail("Se ha producido ImporteInvalidoException");
		} catch (SaldoInsuficienteException e) {
		}
	}

	/*
	 * Caso de prueba: Insertar un importe invalido al retirar dinero Se prueba a
	 * retirar una cantidad de dinero negativa.
	 */
	@Test
	public void testRetirarImporteInvalido() {
		try {
			this.cuentaPepe.retirar(-100);
			fail("Esperaba ImporteInvalidoException");
		} catch (ImporteInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar transferencia Se prueba a realizar una transferencia
	 * correctamente.
	 */
	@Test
	public void testTransferenciaCorrecta() {

		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			this.cuentaAna.addTitular(ana);
			this.cuentaAna.insert();

			this.cuentaPepe.ingresar(1000);
			assertTrue(this.cuentaPepe.getSaldo() == 1000);

			this.cuentaPepe.transferir(this.cuentaAna.getId(), 500, "Alquiler");
			assertTrue(this.cuentaPepe.getSaldo() == 495);
			assertTrue(this.cuentaAna.getSaldo() == 500);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar transferencia con cuenta invalida Se prueba a
	 * realizar una transferencia a una cuenta que no existe.
	 */
	@Test
	public void testTransferenciaCuentaInvalida() {

		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			long destino = 1234;

			this.cuentaPepe.ingresar(1000);
			assertTrue(this.cuentaPepe.getSaldo() == 1000);

			this.cuentaPepe.transferir(destino, 500, "Alquiler");
			fail("Esperaba CuentaInvalidaException");
		} catch (CuentaInvalidaException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una transferencia a la misma cuenta Se prueba a
	 * realizar una transferencia a la misma cuenta
	 */
	@Test
	public void testTransferenciaMismaCuenta() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			cuentaPepe.transferir(1L, 100, "Prueba");
			fail("Esperaba CuentaInvalidaException");
		} catch (CuentaInvalidaException e) {
		} catch (Exception e) {
			fail("Se ha lanzado una excepción inesperada: " + e);
		}

	}

	/*
	 * Caso de prueba: Realizar transferencia con un importe invalido Se prueba a
	 * realizar una transferencia de 0 euros.
	 */
	@Test
	public void testTransferenciaImporteInvalido() {

		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			this.cuentaAna.addTitular(ana);
			this.cuentaAna.insert();

			this.cuentaPepe.ingresar(1000);
			assertTrue(this.cuentaPepe.getSaldo() == 1000);

			this.cuentaPepe.transferir(this.cuentaAna.getId(), 0, "Alquiler");
			fail("Esperaba ImporteInvalidoException");
		} catch (ImporteInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar transferencia sin saldo suficiente Se prueba a
	 * realizar una transferencia dando mas saldo del que contiene la cuenta
	 * original.
	 */
	@Test
	public void testTransferenciaSaldoInsuficiente() {

		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			this.cuentaAna.addTitular(ana);
			this.cuentaAna.insert();

			this.cuentaPepe.ingresar(1000);
			assertTrue(this.cuentaPepe.getSaldo() == 1000);

			this.cuentaPepe.transferir(this.cuentaAna.getId(), 1500, "Alquiler");
			fail("Esperaba SaldoInsuficienteException");
		} catch (SaldoInsuficienteException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}
	
	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de débito a través de un
	 * cliente no autorizado Se prueba a emitir una tarjeta de débito a través de un
	 * cliente que no está autorizado a realizar dicha acción en la cuenta
	 */
	@Test
	public void testEmisionTarjetaDebitoClienteNoAutorizado() {
		try {
			this.cuentaPepe.emitirTarjetaDebito(this.pepe.getNif());
		} catch (ClienteNoAutorizadoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de débito a través de un
	 * cliente que no existe en la base de datos Se prueba a emitir una tarjeta de
	 * débito a través de un cliente que no está en la base de datos
	 */
	@Test
	public void testEmisionTarjetaDebitoClienteNoEncontrado() {
		try {
			this.cuentaPepe.emitirTarjetaDebito("1234");
			fail("Esperaba ClienteNoEncontradoException");
		} catch (ClienteNoEncontradoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de débito a través de un
	 * cliente que tiene un NIF diferente Se prueba a emitir una tarjeta de débito a
	 * través de un cliente que se encuentra en la base de datos pero no coincide
	 * con el titular de la cuenta
	 */
	@Test
	public void testEmisionTarjetaDebitoDiferentNIF() {

		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			ana.insert();
			cuentaPepe.emitirTarjetaDebito(ana.getNif());
			fail("Esperaba ClienteNoAutorizadoException");
		} catch (ClienteNoAutorizadoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}

	}

	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de débito de manera
	 * correcta Se prueba a emitir una tarjeta de débito a través de un cliente que
	 * es titular de la cuenta, sin que ocurra ningún tipo de error
	 */
	@Test
	public void testEmisionTarjetaDebitoCorrecta() {
		try {
			pepe.insert();
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			assertNotNull(this.cuentaPepe.emitirTarjetaDebito(pepe.getNif()));
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Añadir un titular a una cuenta ya creada Se prueba a añadir
	 * un titular a una cuenta en la que ya se había introducido previamente un
	 * titular
	 */
	@Test
	public void testAñadirTitularCuentaCreada() {
		try {
			this.cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			this.cuentaPepe.addTitular(ana);
			fail("Esperaba CuentaYaCreadaException");
		} catch (CuentaYaCreadaException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar un retiro forzoso Se prueba a realizar un retiro
	 * forzoso con un importe menor al saldo disponible
	 */
	@Test
	public void testRetiroForzoso() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(200);
			this.cuentaPepe.retiroForzoso(100, "Prueba");
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}

	}

	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de crédito de manera
	 * correcta Se prueba a emitir una tarjeta de crédito a través de un cliente que
	 * es titular de la cuenta, sin que ocurra ningún tipo de error
	 */
	@Test
	public void testEmitirTarjetaCreditoCorrecta() {

		try {
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			assertNotNull(this.cuentaPepe.emitirTarjetaCredito(pepe.getNif(), 1000));
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}

	}

	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de crédito a través de un
	 * cliente que no existe en la base de datos Se prueba a emitir una tarjeta de
	 * crédito a través de un cliente que no está en la base de datos
	 */
	@Test
	public void testEmitirTarjetaCreditoClienteNoEncontrado() {
		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.emitirTarjetaCredito("1234", 1000);
			fail("Esperaba ClienteNoEncontradoException");
		} catch (ClienteNoEncontradoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Realizar una emisión de la tarjeta de crédito a través de un
	 * cliente no autorizado Se prueba a emitir una tarjeta de crédito a través de
	 * un cliente que no está autorizado a realizar dicha acción en la cuenta
	 */
	@Test
	public void testEmitirTarjetaCreditoClienteNoAutorizado() {

		try {
			ana.insert();
			pepe.insert();
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.emitirTarjetaCredito(ana.getNif(), 1000);
			fail("Esperaba ClienteNoAutorizadoException");
		} catch (ClienteNoAutorizadoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}

	}

	// HASTA AQUÍ HE LLEGAO

	/*
	 * Caso de prueba: Comprobar que se puede comprar con la tarjeta de crédito Se
	 * prueba a crear una cuenta y posteriormente se comprueba que al comprar queda
	 * en la tarjeta de credito el saldo correspondiente
	 */

	@Test
	public void testCompraConTC() {

		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();

			cuentaPepe.ingresar(1000);
			cuentaPepe.retirar(200);
			;
			assertTrue(cuentaPepe.getSaldo() == 800);

			TarjetaCredito tc = cuentaPepe.emitirTarjetaCredito("12345X", 1000);
			tc.comprar(tc.getPin(), 300);
			assertTrue(tc.getCreditoDisponible() == 700);
			tc.liquidar();
			assertTrue(tc.getCreditoDisponible() == 1000);
			assertTrue(cuentaPepe.getSaldo() == 500);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	/*
	 * Caso de prueba: Comprobar que se puede hacer una transferencia Se prueba a
	 * crear dos cuentas y posteriormente se comprueba que transferir dinero de una
	 * cuenta a otra, estas tienen su saldo correspondiente
	 */

	@Test
	public void testTransferenciaOK() {

		try {
			cuentaPepe.addTitular(pepe);
			cuentaPepe.insert();
			cuentaPepe.ingresar(1000);
			cuentaAna.addTitular(ana);
			cuentaAna.insert();
			cuentaPepe.transferir(2L, 500, "Alquiler");
			assertTrue(cuentaPepe.getSaldo() == 495);
			assertTrue(cuentaAna.getSaldo() == 500);
		} catch (Exception e) {
			fail("Excepción inesperada");
		}
	}
}