/*
 * SAE - Sistema de Agenda Electronica
 * Copyright (C) 2009  IMM - Intendencia Municipal de Montevideo
 *
 * This file is part of SAE.

 * SAE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uy.gub.imm.sae.business.ejb.facade;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import uy.gub.imm.sae.business.dto.AtencionLLamadaReporteDT;
import uy.gub.imm.sae.business.dto.RecursoDTO;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.exception.UserException;

public interface Consultas {

	
	public Reserva consultarReservaId(Integer id, Integer recId) throws UserException;
	public Reserva consultarReservaPorNumero(Recurso r, Date fechaHoraInicio, Integer numero) throws UserException;

	public List<ReservaDTO> consultarReservasPorPeriodoEstado(Recurso recurso, VentanaDeTiempo periodo, List<Estado> estados, Boolean atencionPresencial) throws UserException;
	public List<ReservaDTO> consultarReservasEnEspera(Recurso recurso, Boolean atencionPresencial, TimeZone timezone) throws UserException;
	public List<ReservaDTO> consultarReservasEnEsperaUtilizadas(Recurso recurso, Boolean atencionPresencial, TimeZone timezone) throws UserException;
	public List<Reserva> consultarReservaDatos(List<DatoReserva> datos ,Recurso recurso);
	public List<Reserva> consultarReservaDatosPeriodo(List<DatoReserva> datos, Recurso recurso, Date fechaDesde, Date fechaHasta, String codigoTramite, Integer idReserva);	
	public List<ReservaDTO> consultarReservasUsadasPeriodo(Recurso recurso, VentanaDeTiempo periodo, Boolean atencionPresencial) throws UserException;
	public List<Reserva> consultarReservasParaModificarCancelar(List<DatoReserva> datos, Recurso recurso, String codigoSeguridadReserva, TimeZone timezone);
	
	public List<ReservaDTO> consultarReservasCanceladas(Recurso recurso, String codigoTramite, Date reservaFechaDesde, Date reservaFechaHasta, 
      Date creacionFechaDesde, Date creacionFechaHasta, Date cancelacionFechaDesde, Date cancelacionFechaHasta);
	
	public List<Atencion> consultarTodasAtencionesPeriodo(Date fechaDesde,Date fechaHasta);
	public List<AtencionLLamadaReporteDT> consultarLlamadasAtencionPeriodo(Date fechaDesde, Date fechaHasta);
  public List<AtencionLLamadaReporteDT> consultarAtencionesPresencialesRecursoPeriodo(Recurso recurso, Date fechaDesde, Date fechaHasta);
	
  public List<Map<String, Object>> consultarReservasPorTokenYDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc, String codTramite) throws UserException;
  public List<Map<String, Object>> consultarReservasPorTokenYAgendaTramiteDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc, String codTramite, Date fechaDesde, Date fechaHasta) throws UserException;
  public List<Map<String, Object>> consultarReservasVigentesPorTokenYAgendaTramiteDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc, String codTramite) throws UserException;
  public List<Map<String, Object>> consultarReservasVigentesPorTokenYEmpresaTramiteDocumento(String token, String idEmpresa, String codTramite, List<String[]> personas) throws UserException;
  
  public boolean validarTokenEmpresa(String token, Integer idEmpresa);
  public Map<String, Object> consultarRecursosPorAgenda(Integer idEmpresa, Integer idAgenda, String idioma) throws UserException;
  public Map<String, Object> consultarDisponibilidadesPorRecurso(Integer idEmpresa, Integer idAgenda, Integer idRecurso, String idioma) throws UserException;
  public Map<String, Object> consultarDisponibilidadesPorRecursoTiempo(Integer idEmpresa, Integer idAgenda, Integer idRecurso, String idioma, VentanaDeTiempo ventana) throws UserException;
	
	public Map<String, Object> consultarDatosEmpresa(Integer idEmpresa);
	
	public String consultarConfiguracion(String clave);
	
	public List<RecursoDTO> consultarCambiosEnUnRecurso(String token, Integer idEmpresa, Integer idAgenda, Integer idRecurso, Date fechaDesde, Date fechaHasta) throws UserException;
	
}
