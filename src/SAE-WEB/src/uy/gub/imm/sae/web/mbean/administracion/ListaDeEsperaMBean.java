package uy.gub.imm.sae.web.mbean.administracion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.context.FacesContext;

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.subtable.SubTable;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.ejb.facade.Llamadas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinReservaClient;
import uy.gub.imm.sae.web.common.reporte.Columna;

public class ListaDeEsperaMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/LlamadasBean!uy.gub.imm.sae.business.ejb.facade.LlamadasRemote")
	private Llamadas llamadasEJB;

	private ListaDeEsperaSessionMBean listaDeEsperaSessionMBean;
	private SessionMBean sessionMBean;

	private List<Columna> definicionColumnas;
	
	private Column columnaHoraListaDeEspera;
	private DataTable tablaReservas;
	private SubTable subTablaListaDeEspera;
	
	private List<SelectItem> itemsEstado;
	
  public void beforePhaseListaDeEspera(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_FATENCION", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("lista_de_espera"));
    }
  }
  
  public ListaDeEsperaMBean() {
  }
  
	@PostConstruct
	public void init() {
		boolean huboError = false;
		if(sessionMBean.getAgendaMarcada()==null) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if(sessionMBean.getRecursoMarcado()==null) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		if(huboError) {
			return;
		}
		try {
			List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
			listaDeEsperaSessionMBean.setAgrupaciones(agrupaciones);
			refrescarHorariosDeEspera(null, listaDeEsperaSessionMBean.getAtencionPresencial());
		} catch (UserException ex) {
			addErrorMessage(ex, MSG_ID);
		}
	}
	
	public ListaDeEsperaSessionMBean getListaDeEsperaSessionMBean() {
		return listaDeEsperaSessionMBean;
	}
	public void setListaDeEsperaSessionMBean(
			ListaDeEsperaSessionMBean listaDeEsperaSessionMBean) {
		this.listaDeEsperaSessionMBean = listaDeEsperaSessionMBean;
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

  public List<Columna> getDefinicionColumnas() {
		if (definicionColumnas == null) {
			definicionColumnas = new ArrayList<Columna>();
			if (sessionMBean.getRecursoMarcado() != null) {
				try {
					//Definicion de los campos dinamicos del reporte
					List<AgrupacionDato> agrupaciones = listaDeEsperaSessionMBean.getAgrupaciones();
					for(AgrupacionDato grupo: agrupaciones) {
						for(DatoASolicitar campo: grupo.getDatosASolicitar()) {
							if (campo.getIncluirEnReporte()) {
								Columna col = new Columna();
								col.setId(campo.getNombre());
								col.setNombre(campo.getEtiqueta());
								col.setClase(String.class);
								col.setAncho(campo.getAnchoDespliegue());
								definicionColumnas.add(col);
							}
						}
					}
		
				} catch (Exception e) {
					addErrorMessage(e, MSG_ID);
				}
			}
		}
		return definicionColumnas;
	}
	
	public Column getColumnaHoraListaDeEspera() {
		return columnaHoraListaDeEspera;
	}

	public void setColumnaHoraListaDeEspera(Column columnaHoraListaDeEspera) {
		this.columnaHoraListaDeEspera = columnaHoraListaDeEspera;
		//Seteo el colspan de la columna de horas, para adecuarlo a la cantidad dinamica de campos a desplegar
		columnaHoraListaDeEspera.setColspan(getDefinicionColumnas().size() + 2);
	}

	public DataTable getTablaReservas() {
		return tablaReservas;
	}
	
	public void setTablaReservas(DataTable tablaReservas) {
		this.tablaReservas = tablaReservas;
	}
	
	public SubTable getSubTablaListaDeEspera() {
		return subTablaListaDeEspera;
	}

	public void setSubTablaListaDeEspera(SubTable subTablaListaDeEspera) {
		this.subTablaListaDeEspera = subTablaListaDeEspera;
	}
	
	@SuppressWarnings("unchecked")
	public void cambiaSeleccionEstados(ValueChangeEvent event) {
		List<Estado> estados = (List<Estado>) event.getNewValue();
		refrescarHorariosDeEspera(estados, listaDeEsperaSessionMBean.getAtencionPresencial());
	}

	/**
	 * Marca la reserva indicando que el el usuario estuvo presente en la cita
	 */
	//public void asistio(ActionEvent event) {
	public void asistio() {
		try {
			Reserva reserva = listaDeEsperaSessionMBean.getSiguienteReserva();
			llamadasEJB.marcarAsistencia(sessionMBean.getEmpresaActual(), sessionMBean.getRecursoMarcado(), reserva);
			cierroDatosSiguiente();
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
			ex.printStackTrace();
		}
	}
	/**
	 * Marca la reserva indicando que el el usuario estuvo ausente en la cita
	 */
	public void falto() {
		try {
			Reserva reserva = listaDeEsperaSessionMBean.getSiguienteReserva();
			llamadasEJB.marcarInasistencia(sessionMBean.getEmpresaActual(), sessionMBean.getRecursoMarcado(), reserva);
			cierroDatosSiguiente();
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
		}
	}
	
	private void cierroDatosSiguiente() {
		refrescarHorariosDeEspera(null, listaDeEsperaSessionMBean.getAtencionPresencial());
		listaDeEsperaSessionMBean.setMostrarDatosSiguiente(false);
	}

	private void refrescarHorariosDeEspera(List<Estado> estados, boolean atencionPresencial) {
		listaDeEsperaSessionMBean.setHorarios(new ArrayList<Horario>());
		if (sessionMBean.getRecursoMarcado() != null) {
			try {
				if (getListaDeEsperaSessionMBean().getEstadosSeleccionado() == null) {
					getListaDeEsperaSessionMBean().setEstadosSeleccionado(new ArrayList<Estado>());
					getListaDeEsperaSessionMBean().getEstadosSeleccionado().add(Estado.R);
				}
				if(estados == null) {
					estados = getListaDeEsperaSessionMBean().getEstadosSeleccionado();
				}
		    Recurso recursoMarcado = sessionMBean.getRecursoMarcado();
				List<ReservaDTO> reservas = llamadasEJB.obtenerReservasEnEspera(recursoMarcado, estados, atencionPresencial, sessionMBean.getTimeZone());
				Horario horario = null;
				//Recorro las reservas agrupándolas por horario
				for (ReservaDTO reserva : reservas) {
          //Si no es presencial, si el horario es nulo o la reserva no tiene el mismo horario 
				  //se crea un nuevo grupo con esta reserva, sino se agrega al grupo actual
				  if (horario == null || (!atencionPresencial && !reserva.getHoraInicio().equals(horario.getHora()))) {
						horario = new Horario();
						horario.setHora(reserva.getHoraInicio());
						horario.getListaEspera().add(crearEspera(reserva));
						listaDeEsperaSessionMBean.getHorarios().add(horario);
					} else {
						horario.getListaEspera().add(crearEspera(reserva));
					}
				}
			} catch (UserException uEx) {
				addErrorMessage(uEx, MSG_ID);
			}
		}
	}
	
	public UIComponent getCamposSiguienteReserva() {
		return listaDeEsperaSessionMBean.getCamposSiguienteReserva();
	}
	
	public void setCamposSiguienteReserva(UIComponent camposSiguienteReserva) {
	  listaDeEsperaSessionMBean.setCamposSiguienteReserva(camposSiguienteReserva);;
	}

	public Boolean getMostrarDatosSiguiente() {
		return listaDeEsperaSessionMBean.getMostrarDatosSiguiente();
	}
	
	//Llama a la capa de negocio consumiendo la siguiente reserva en la lista de espera y la despliega al usuario.
	public void siguiente(ActionEvent event) {
		Integer iPuesto = null;
		try {
			iPuesto = Integer.valueOf(listaDeEsperaSessionMBean.getPuesto());
		}catch(Exception ex) {
			addErrorMessage(sessionMBean.getTextos().get("el_numero_de_puesto_no_es_valido"));
			return;
		}
		if (sessionMBean.getRecursoMarcado() != null) {
			Reserva siguienteReserva = null;
			try {
			  //Obtener la siguiente reserva (no se hace con los datos en sesión porque es posible que otros usuarios hayan hecho más llamadas) 
				siguienteReserva = llamadasEJB.siguienteEnEspera(sessionMBean.getRecursoMarcado(), iPuesto, listaDeEsperaSessionMBean.getAtencionPresencial());
				listaDeEsperaSessionMBean.setSiguienteReserva(siguienteReserva);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
			listaDeEsperaSessionMBean.getCamposSiguienteReserva().getChildren().clear();
			if (siguienteReserva != null) {
				try {
					List<AgrupacionDato> agrupaciones = listaDeEsperaSessionMBean.getAgrupaciones();
					FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(), siguienteReserva, listaDeEsperaSessionMBean.getCamposSiguienteReserva(), agrupaciones, sessionMBean.getFormatoFecha());
				} catch (Exception e) {
					addErrorMessage(e, MSG_ID);
				}
				
				listaDeEsperaSessionMBean.setMostrarDatosSiguiente(true); //Para que al rerenderizar se muestre el formulario con los datos de la siguiente reserva
				
			}
		}		

	}

	//Llama a la capa de negocio re-consumiendo la reserva indicada y la despliega al usuario.
	public void llamar(ActionEvent event) {
		Integer iPuesto = null;
		try {
			iPuesto = Integer.valueOf(listaDeEsperaSessionMBean.getPuesto());
		}catch(Exception ex) {
			addErrorMessage(sessionMBean.getTextos().get("el_numero_de_puesto_no_es_valido"));
			return;
		}
		
		Reserva siguienteReserva = null;
		try {
			Espera espera = (Espera)getSubTablaListaDeEspera().getRowData();
			if (espera != null) {
				Reserva r = new Reserva();
				r.setId(espera.getReserva().getId());
				siguienteReserva = llamadasEJB.volverALlamar(sessionMBean.getRecursoMarcado(), iPuesto, r);
				listaDeEsperaSessionMBean.setSiguienteReserva(siguienteReserva);
			}
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
		
		listaDeEsperaSessionMBean.getCamposSiguienteReserva().getChildren().clear();
		
		if (siguienteReserva != null) {
			try {
				List<AgrupacionDato> agrupaciones = listaDeEsperaSessionMBean.getAgrupaciones();
				FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(), siguienteReserva,	listaDeEsperaSessionMBean.getCamposSiguienteReserva(), agrupaciones, sessionMBean.getFormatoFecha());
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
			listaDeEsperaSessionMBean.setMostrarDatosSiguiente(true); //Para que al rerenderizar se muestre el formulario con los datos de la siguiente reserva
		}
	}
	
	public List<SelectItem> getItemsEstado() {
		
		if (itemsEstado == null) {
			List<Estado> valorItem1 = new ArrayList<Estado>();
			valorItem1.add(Estado.R);
			List<Estado> valorItem2 = new ArrayList<Estado>();
			valorItem2.add(Estado.U);
			itemsEstado = new ArrayList<SelectItem>();
			itemsEstado.add(new SelectItem(valorItem1, "Pendientes"));
			itemsEstado.add(new SelectItem(valorItem2, "Llamadas"));
		}
		
		return itemsEstado;
	}

	public String getNombreColumnaPuesto() {
		Recurso recurso = sessionMBean.getRecursoMarcado() ;
		if(recurso != null) {
			TextoRecurso textoRecurso = getTextoRecurso(recurso, sessionMBean.getIdiomaActual());
			if (textoRecurso!=null && textoRecurso.getTituloPuestoEnLlamador()!=null) {
				return textoRecurso.getTituloPuestoEnLlamador();
			}
		}
		return "";
	}
	
	private Espera crearEspera (ReservaDTO reserva) {
		Espera espera = new Espera();
		espera.setReserva(reserva);
		List<Columna> cols = getDefinicionColumnas();
		for (Columna columna : cols) {
			Object dato = reserva.getDatos().get(columna.getId());
			//Si el dato es nulo, por ejemplo campo opcional agrego vacio
			espera.getDatos().add((dato == null ? "" : dato.toString()));
		}
		
		return espera;
	}
	
	public class Horario {
		
		private Date hora;
		private List<Espera> listaEspera = new ArrayList<Espera>();
		
		public Date getHora() {
			
			return hora;
		}
		public void setHora(Date hora) {
			this.hora = hora;
		}
		public List<Espera> getListaEspera() {
			return listaEspera;
		}
		public void setListaEspera(List<Espera> listaEspera) {
			this.listaEspera = listaEspera;
		}
	}

	public class Espera {
		
		private ReservaDTO reserva;
		private List<String> datos = new ArrayList<String>();

		public ReservaDTO getReserva() {
			return reserva;
		}
		public void setReserva(ReservaDTO reserva) {
			this.reserva = reserva;
		}
		public List<String> getDatos() {
			return datos;
		}
		public void setDatos(List<String> datos) {
			this.datos = datos;
		}
	}
	
	public void cambioAtencionPresencial() {
	  refrescarHorariosDeEspera(null, listaDeEsperaSessionMBean.getAtencionPresencial());
	}
	
  public Boolean getAtencionPresencial() {
    return listaDeEsperaSessionMBean.getAtencionPresencial();
  }

  public void setAtencionPresencial(Boolean atencionPresencial) {
    listaDeEsperaSessionMBean.setAtencionPresencial(atencionPresencial);
  }
	
  public String getPuesto() {
    return listaDeEsperaSessionMBean.getPuesto();
  }

  public void setPuesto(String puesto) {
    listaDeEsperaSessionMBean.setPuesto(puesto);
  }

	
}
