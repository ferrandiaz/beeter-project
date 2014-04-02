package edu.upc.eetac.dsa.ferrandiaz.beeter.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.ferrandiaz.beeter.api.model.user;
import edu.upc.eetac.dsa.ferrandiaz.beeter.api.model.Sting;
import edu.upc.eetac.dsa.ferrandiaz.beeter.api.model.StingCollection;

@Path("/username/{username}")
public class UserResources {

	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	@GET
	@Produces(MediaType.BEETER_API_USER)
	// Funcion que retorna el usuario pasado por parametro
	public user getUser(@PathParam("username") String username) {
		user usuario = new user();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement(buildGetUserByUsername());
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				usuario.setUsername(rs.getString("username"));
				usuario.setNombre(rs.getString("name"));
				usuario.setEmail(rs.getString("email"));
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return usuario;

	}

	public String buildGetUserByUsername() {
		return "select * from users where username = ?";
	}

	@Path("/stings")
	@GET
	@Produces(MediaType.BEETER_API_STING_COLLECTION)
	public StingCollection getStings(@PathParam("username") String username) {
		StingCollection stings = new StingCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement(buildGetStingsByUsername());
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Sting sting = new Sting();
				sting.setSubject(rs.getString("subject"));
				sting.setContent(rs.getString("content"));
				stings.addSting(sting);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return stings;

	}

	public String buildGetStingsByUsername() {
		return "select * from stings where username = ?";
	}

	@PUT
	@Consumes(MediaType.BEETER_API_STING)
	@Produces(MediaType.BEETER_API_USER)
	public user updateUser(@PathParam("username") String username, user user) {
		validateUsername(username);
		validateUser(user);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement(buildUpdateUser(),
					Statement.RETURN_GENERATED_KEYS);
			// System.out.println("Username: " + user.getUsername() + " Nombre "
			// + user.getNombre() + " Email " + user.getEmail());
			stmt.setString(1, user.getNombre());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, username);
			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUser(username);

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;

	}

	private void validateUsername(String username) {
		user currentuser = getUser(username);
		if (!security.getUserPrincipal().getName()
				.equals(currentuser.getUsername()))
			throw new ForbiddenException(
					"You are not allowed to modify this user.");
	}

	private void validateUser(user user) {
		if (user.getNombre() == null && user.getEmail() == null) {
			throw new BadRequestException("Name and Email can't be null.");
		}
	}

	public String buildUpdateUser() {
		return "update users set name=ifnull(?, name), email=ifnull(?, email) where username=?";
	}
}
