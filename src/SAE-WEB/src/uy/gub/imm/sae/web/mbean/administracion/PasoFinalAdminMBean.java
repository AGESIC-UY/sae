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

package uy.gub.imm.sae.web.mbean.administracion;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.utilidades.Metavariables;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;

import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;


/**
 * Presenta todos los datos de la reserva y da la opción de imprimir un recibo.
 * @author im2716295
 *
 */
public class PasoFinalAdminMBean extends BaseMBean {

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	private SessionMBean sessionMBean;
	
	private List<DatoDelRecurso> infoRecurso;
	
	private UIComponent campos;
	
	private Logger logger = Logger.getLogger(PasoFinalAdminMBean.class);

	
	@PostConstruct
	public void init() {
		if (sessionMBean.getAgenda() == null || sessionMBean.getRecurso() == null || sessionMBean.getReservaConfirmada() == null) {
			redirect("inicio");
			return;
		}
	}	

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	
	public String getAgendaNombre() {
		if (sessionMBean.getAgenda() != null) {
			return sessionMBean.getAgenda().getNombre();
		}else {
			return null;
		}
	}	
	
	public String getEtiquetaDelRecurso() {
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgenda(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		}	else {
			return null;
		}
	}
	
	public List<DatoDelRecurso> getInfoRecurso() {
		if (infoRecurso == null) {
			if (sessionMBean.getRecurso() != null) {
				try {
					infoRecurso = recursosEJB.consultarDatosDelRecurso(sessionMBean.getRecurso());
					if (infoRecurso.isEmpty()) {
						infoRecurso = null;
					}
				} catch (Exception e) {
					addErrorMessage(e);
				}
			}
		}
		return infoRecurso;
	}	
	
	public UIComponent getCampos() {
		return campos;
	}
	
	public void setCampos(UIComponent campos) {
		this.campos = campos;
		String mensajeError = "";
		try {
			Recurso recurso = sessionMBean.getRecurso();
			if (campos.getChildCount() == 0 && recurso != null) {
				mensajeError = "RESERVA: ";
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sessionMBean.getTimeZone());
				Reserva rtmp = sessionMBean.getReservaConfirmada();
				if (rtmp == null) {
					mensajeError += "nulo";
				}	else {
					mensajeError += rtmp.getId() + ":" + rtmp.getFechaCreacion() + ":";
					if (rtmp.getDatosReserva()== null) {
						mensajeError += "DatosReserva nulo";
					}
				}
				Map<String, Object> valores = obtenerValores(sessionMBean.getReservaConfirmada().getDatosReserva());
				FormularioDinamicoReserva formularioDin = new FormularioDinamicoReserva(valores, sessionMBean.getFormatoFecha());
				formularioDin.armarFormulario(agrupaciones, null);
				UIComponent formulario = formularioDin.getComponenteFormulario();
				campos.getChildren().add(formulario);
			}
		} catch (Exception e) {
			logger.error(mensajeError, e);
			redirect(ERROR_PAGE_OUTCOME);
		}
	}

  public String getDescripcion() {
	try{  
	    TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgenda(), sessionMBean.getIdiomaActual());
	    if(textoAgenda != null) {
	      String str = textoAgenda.getTextoTicketConf();
	      if(str!=null) {
	        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	        String linkBase = request.getScheme()+"://"+request.getServerName();
	        if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
	          linkBase = linkBase + ":" + request.getServerPort();
	        }
	        Agenda agenda = sessionMBean.getAgenda();
	        Reserva reserva = sessionMBean.getReservaConfirmada();
	        Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
	        String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+sessionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+reserva.getId();
	        String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+sessionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&r="+recurso.getId()+"&ri="+reserva.getId();
	        str = Metavariables.remplazarMetavariables(str, reserva, sessionMBean.getFormatoFecha(), sessionMBean.getFormatoHora(), linkCancelacion, linkModificacion);
	        return str;
	      } else {
	        return "";
	      }
	    }
	    else {
	      return "";
	    }
	}
	catch (Exception e) {
		logger.error(e.getMessage());
		return "";
	}
	
	
  }

	/**
	 * @param datos de alguna reserva
	 * @return retorna los valores de cada dato en un mapa cuya clave es el nombre del campo 
	 */
	private Map<String, Object> obtenerValores(Set<DatoReserva> datos) {
		Map<String, Object> valores = new HashMap<String, Object>();
		for (DatoReserva dato : datos) {
			valores.put(dato.getDatoASolicitar().getNombre(), dato.getValor());
		}
		return valores;
	}
	
	public void beforePhase (PhaseEvent event) {
		disableBrowserCache(event);
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("realizar_reserva"));
		}
	}
	
	public Date getDiaSeleccionado() {
		return sessionMBean.getDiaSeleccionado();
	}

	public Date getHoraSeleccionada() {
		if (sessionMBean.getDisponibilidad() != null) {
			return sessionMBean.getDisponibilidad().getHoraInicio();
		} else {
			return null;
		}
	}

	public String getReservaNumero() {
		if(sessionMBean.getReservaConfirmada() != null) {
			return "No. " + sessionMBean.getReservaConfirmada().getNumero();
		}
		return "";
	}
	
	public String getRecursoDescripcion() {
		Recurso recurso = sessionMBean.getRecurso();
		if (recurso != null) {
			String descripcion = recurso.getNombre();
			if(descripcion != null && !descripcion.equals(recurso.getDireccion())) {
				descripcion = descripcion + " - " + recurso.getDireccion();
			}
			return  descripcion;
		} else {
			return null;
		}
	}
	
}
