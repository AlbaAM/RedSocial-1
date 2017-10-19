package com.intravita.proyectointranet.persistencia;

import java.util.LinkedList;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.springframework.stereotype.Component;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import com.intravita.proyectointranet.modelo.Usuario;
import com.intravita.proyectointranet.persistencia.MongoBroker;
import com.intravita.proyectointranet.persistencia.UsuarioDAO;

@Component
public class UsuarioDAOImpl implements UsuarioDAO {
	
	public UsuarioDAOImpl() {
		super();
	}
	/**
	 * @method login
	 * @param usuario
	 * @return true si login es correcto, false en caso opuesto
	 */
	public boolean login(Usuario usuario) {
		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		BsonDocument criterio = new BsonDocument();
		criterio.append("nombre", new BsonString(usuario.getNombre()));
		criterio.append("pwd", new BsonString(DigestUtils.md5Hex(usuario.getClave())));
		FindIterable<BsonDocument> resultado=usuarios.find(criterio);
		BsonDocument usuarioBson = resultado.first();
		if (usuarioBson==null) {
			return false;
		}
		return true;
	}

	/**
	 * @method insercion de usuarios con y sin encriptar clave
	 * @param usuario
	 * @return true si se ha insertado en la bbdd false en caso opuesto
	 */	
	public boolean insert (Usuario usuario){
		BsonDocument bso = new BsonDocument();
		bso.append("nombre", new BsonString(usuario.getNombre()));
		bso.append("pwd", new BsonString(DigestUtils.md5Hex(usuario.getClave())));
		bso.append("email", new BsonString(usuario.getEmail()));
		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		FindIterable<BsonDocument> resultado=usuarios.find(bso);
		BsonDocument usuarioBso = resultado.first();
		if (usuarioBso==null) {
			usuarios.insertOne(bso);
		}
		return login(usuario);
	}
	public void insertSinEncrypt (Usuario usuario){
		BsonDocument bso = new BsonDocument();
		bso.append("nombre", new BsonString(usuario.getNombre()));
		bso.append("pwd", new BsonString(usuario.getClave()));
		bso.append("email", new BsonString(usuario.getEmail()));
		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		FindIterable<BsonDocument> resultado=usuarios.find(bso);
		BsonDocument usuarioBso = resultado.first();
		if (usuarioBso==null) {
			usuarios.insertOne(bso);
		}
	}
	/***
	 * @method select con nombre que devuelve todos los datos del usuario
	 * @param nombre
	 * @return usuario completo
	 */
	public Usuario selectNombre(String nombreParam) {
		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		BsonDocument criterio = new BsonDocument();
		criterio.append("nombre", new BsonString(nombreParam));
		FindIterable<BsonDocument> resultado=usuarios.find(criterio);
		BsonDocument usuario = resultado.first();
		Usuario result;
		if (usuario==null) {
			return null;
		}
		else {
			BsonValue nombre=usuario.get("nombre");
			BsonString name=nombre.asString();
			String nombreFinal=name.getValue();
			
			BsonValue pwd=usuario.get("pwd");
			BsonString password=pwd.asString();
			String pwdFinal=password.getValue();
			
			BsonValue email=usuario.get("email");
			BsonString correo=email.asString();
			String emailFinal=correo.getValue();
			result = new Usuario(nombreFinal, pwdFinal, emailFinal);
		}
		return result;
	}

	public Usuario select(Usuario generico) {
		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		BsonDocument criterio = new BsonDocument();
		criterio.append("nombre", new BsonString(generico.getNombre()));
		criterio.append("pwd", new BsonString(DigestUtils.md5Hex(generico.getClave())));
		FindIterable<BsonDocument> resultado=usuarios.find(criterio);
		BsonDocument usuario = resultado.first();
		Usuario result;
		if (usuario==null) {
			result=new Usuario("-","-");
		}
		else {
			result = new Usuario(generico.getNombre(),generico.getClave());
		}
		return result;
	}

	public void delete (Usuario usuario){
		BsonDocument bso = new BsonDocument();
		bso.append("nombre", new BsonString(usuario.getNombre()));

		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		usuarios.deleteOne(bso);

	}
	
	public void update(String nombre, String pwdAntigua, String pwdNueva){

		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		BsonDocument criterio = new BsonDocument();
		criterio.append("nombre", new BsonString(nombre));
		criterio.append("pwd", new BsonString(pwdAntigua));
		FindIterable<BsonDocument> resultado=usuarios.find(criterio);
		BsonDocument usuario = resultado.first();
		BsonDocument actualizacion= new BsonDocument("$set", new BsonDocument("pwd", new BsonString(pwdNueva)));
		usuarios.findOneAndUpdate(usuario, actualizacion);
	}
	
	public String selectPwd(String nombre){
		
		BsonValue pwd;
		MongoBroker broker = MongoBroker.get();
		MongoCollection<BsonDocument> usuarios = broker.getCollection("Usuarios");
		BsonDocument criterio = new BsonDocument();
		criterio.append("nombre", new BsonString(nombre));
		FindIterable<BsonDocument> resultado=usuarios.find(criterio);
		BsonDocument usuario = resultado.first();
		pwd=usuario.get("pwd");
		BsonString password=pwd.asString();
		String pwdFinal=password.getValue();
		return pwdFinal;
	}

	
	
}
