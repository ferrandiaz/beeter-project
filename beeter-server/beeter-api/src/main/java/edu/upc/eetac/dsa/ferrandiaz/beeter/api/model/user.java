package edu.upc.eetac.dsa.ferrandiaz.beeter.api.model;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.Binding;

import edu.upc.eetac.dsa.ferrandiaz.beeter.api.MediaType;
import edu.upc.eetac.dsa.ferrandiaz.beeter.api.UserResources;
//import edu.upc.eetac.dsa.ferrandiaz.beeter.api.StingResource;
public class user {
	@InjectLinks({
		@InjectLink(resource = UserResources.class, style = Style.ABSOLUTE, rel = "username", title = "username", type = MediaType.BEETER_API_USER),
		@InjectLink(resource = UserResources.class, style = Style.ABSOLUTE, rel = "username", title = "stings", type = MediaType.BEETER_API_STING_COLLECTION, method = "getStings", bindings = @Binding(name = "username", value = "${instance.id}")) })
	private String username;
	private String nombre;
	private String email;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
