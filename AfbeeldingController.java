package jsf.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import data.Afbeeldingen;
import data.User;
import ejb.AfbeeldingManagementEJBLocal;
import ejb.FiliaalManagementEJBLocal;
import ejb.GebruikerManagementEJBLocal;

/**
 * Klasse die afbeeldingen ingegeven door de gebruiker en opgehaald uit de
 * databse behandeld.
 * 
 */

@ManagedBean(name = "AfbeeldingController")
@RequestScoped
public class AfbeeldingController implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Objecten */
	@EJB
	private AfbeeldingManagementEJBLocal afbeeldingEJB;

	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;

	@EJB
	private GebruikerManagementEJBLocal userEJB;

	/* Variabelen */
	private Afbeeldingen afbeelding = new Afbeeldingen();

	private Part file;

	private int filiaalId;

	/* Methoden */

	/**
	 * Methode die de upload door de user verwerkt en omzet naar een bytestream,
	 * deze bytestream wordt dan opgeslagen in de databank. De afbeeldingen
	 * worden ook lokaal opgeslagen voor easy access.
	 * 
	 * 
	 * @see Afbeeldingen
	 * @see afbeeldingEJB
	 * @see Files
	 * @see InputStream
	 * @see Part
	 * @see Path
	 * 
	 * @exception IOException
	 *                Indien fout, wordt er een IOException opgegooid.
	 */
	public void processFileUpload() throws IOException {

		Part uploadedFile = getFile();
		if (uploadedFile != null) {
			Path folder = Paths.get(
					"D:/bureaublad/KUL/BA3/semester 2/project/git/git2/PG3/RetailSonarWeb/WebContent/uploadedImages");

			String filename = FilenameUtils.getBaseName(FilenameUtils.getName(getSubmittedFileName(uploadedFile)));
			String fullname = getSubmittedFileName(uploadedFile);
			String extension = fullname.substring(fullname.indexOf('.') + 1);

			Path file = Files.createTempFile(folder, filename + "-", "." + extension);

			InputStream bytes = null;
			byte[] data = null;

			bytes = uploadedFile.getInputStream(); //
			data = IOUtils.toByteArray(bytes);

			Files.copy(bytes, file, StandardCopyOption.REPLACE_EXISTING);

			afbeelding.setPad(file.getFileName().toString());
			afbeelding.setWaarde(data);
			afbeelding.setFiliaalGegevens(filiaalEJB.findProject(filiaalId));
			afbeeldingEJB.invoegen(afbeelding);
		}
	}

	/**
	 * Methode die uit het upgeloade bestand de filename haalt aan de hand van
	 * de header.
	 * 
	 * @param filePart
	 *            Object van het type {@link Part}.
	 * @return String De naam met extensie van de afbeelding.
	 * @see Part
	 */
	public static String getSubmittedFileName(Part filePart) {
		String header = filePart.getHeader("content-disposition");
		if (header == null)
			return null;
		for (String headerPart : header.split(";")) {
			if (headerPart.trim().startsWith("filename")) {
				return headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	/**
	 * Methode die alle afbeeldingen van een filiaal zoekt op basis van het
	 * filiaalId.
	 * 
	 * 
	 * @return List Een lijst met alle objecten van het type
	 *         {@link Afbeeldingen} die voldoen aan het gegeven filiaalId.
	 * @see Afbeeldingen
	 * @see afbeeldingEJB
	 */
	public List<Afbeeldingen> findAllAfbeeldingenFiliaal() {

		List<Afbeeldingen> temp = afbeeldingEJB.findAllAfbeeldingenFiliaal(filiaalId);
		return temp;

	}

	/**
	 * Methode die alle afbeeldingen van een filiaal zoekt op basis van een
	 * meegegeven filiaalId.
	 * 
	 * @param filiaalId
	 *            Id van het filiaal waarvan de fabeeldingen worden opgevraagd.
	 * @return List Lijsts met alle objecten van het type {@link Afbeeldingen}
	 *         die voldoen aan het opgegeven filiaalId.
	 * @see Afbeeldingen
	 * @see afbeeldingEJB
	 */
	public List<Afbeeldingen> findAllAfbeeldingenFiliaal2(int filiaalId) {
		if (filiaalId != 0) {
			List<Afbeeldingen> temp = afbeeldingEJB.findAllAfbeeldingenFiliaal(filiaalId);
			return temp;
		} else
			return Collections.emptyList();

	}

	/**
	 * Methode die de huidige ingelogde gebruiker zoekt.
	 * 
	 * 
	 * @return User Deze methode geeft een object van het type {@link User}
	 *         terug.
	 * @see User
	 * @see afbeeldingEJB
	 */
	public User findLoggedOnUser() {
		String uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		User loggedInUser = userEJB.findPerson(uname);
		return loggedInUser;
	}

	/* Getters and Setters */
	public int getFiliaalId() {
		return filiaalId;
	}

	public void setFiliaalId(int filiaalId) {
		this.filiaalId = filiaalId;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}
}
