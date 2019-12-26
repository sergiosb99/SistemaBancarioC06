package edu.uclm.esi.iso2.banco20193capas;

import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.uclm.esi.iso2.banco20193capas.model.Cliente;
import edu.uclm.esi.iso2.banco20193capas.model.Cuenta;
import edu.uclm.esi.iso2.banco20193capas.model.Manager;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCliente extends TestCase {
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
	 * Caso de prueba: Cambiar Id del cliente
	 */

	@Test
	public void testCambiarIdCliente() {
		try {
			pepe.setId((long) 23423);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}
	/*
	 * Caso de prueba: Cambiar Nombre del cliente
	 */

	@Test
	public void testCambiarNombreCliente() {
		try {
			pepe.setNombre("DON PEPE");
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}

	/*
	 * Caso de prueba: Cambiar Apellidos del cliente
	 */

	@Test
	public void testCambiarApellidosCliente() {
		try {
			pepe.setApellidos("Y LOS GLOBOS");
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}
	/*
	 * Caso de prueba: Comprobar nombre del cliente
	 */

	@Test
	public void testComprobarNombreCliente() {
		try {
			if (pepe.getNombre() != "Pepe")
				fail("El nombre debe ser Pepe");
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}

	/*
	 * Caso de prueba: Comprobar apellidos del cliente
	 */

	@Test
	public void testComprobarApellidosCliente() {
		try {
			if (pepe.getApellidos() != "Pérez")
				fail("El apellido debe ser Pérez");
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}

	/*
	 * Caso de prueba: Cambiar Nif del cliente
	 */
	@Test
	public void testCambiarNIFCliente() {
		try {
			pepe.setNif("NIF");
		} catch (Exception e) {
			fail("Excepción inesperada: " + e);
		}
	}
}
