package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import db.DB;
import db.DbException;
import model.DAO.DepartmentDAO;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDAO {
	
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		
		try {
			
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(
					"INSERT INTO department (Name) VALUES(?)",Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNome());
			
			int linhaIncluida = st.executeUpdate();
			
			//c?digo para obter o Id criado no banco de dados e associalo ao objeto 
			//fornecido como par?metro
			if(linhaIncluida > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultset(rs);
			}
			else {
				throw new SQLException("Erro ao criar novo Departamento");
			}
			conn.commit();					
		}catch(SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Erro: "+e.getMessage());
			}catch(SQLException e2) {
				throw new DbException("Erro ao cancelar cria??o!: "+e2.getMessage());
			}
		}finally {
			DB.closeStatement(st);
		
		}	
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(
					"UPDATE department SET Name = ? WHERE Id = ?");
			
			st.setString(1, obj.getNome());
			st.setInt(2, obj.getId());
			
			int resultado = st.executeUpdate();
			
			if(resultado == 0) {
				throw new SQLException("Departamento n?o encontrado!");
			}
			conn.commit();
		}catch(SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Erro : " + e.getMessage());
			}catch(SQLException e2 ) {
				throw new DbException("Erro ao voltar a transa??o no B. Dados!");
			}
		}finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(
					"DELETE FROM department WHERE Id = ? ");
			st.setInt(1, id);
			
			int registroApagado = st.executeUpdate();
			
			if(registroApagado == 0) {
				throw new SQLException("Departamento n?o existe!");
			}
			conn.commit();
		}catch(SQLException e){
			try {
				conn.rollback();
				throw new DbException("Erro: "+e.getMessage());
			}catch(SQLException e2) {
				throw new DbException("Erro ao cancelar exclus?o! \n\n"+e2.getMessage() );
			}
		}finally {
			DB.closeStatement(st);
		}
				
		
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT department.* FROM department WHERE Id= ?");
			st.setInt(1, id);
			
			rs = st.executeQuery();
			if(rs.next()) {
				Department dep = new Department(rs.getInt("Id"), rs.getString("Name"));
				return dep;
			}
			else {
				throw new SQLException("Departamento n?o encontrado!");
			}
		}catch(SQLException e) {
			throw new DbException("Erro: "+e.getMessage());
		}
		finally {
			DB.closeResultset(rs);
			DB.closeStatement(st);
		}
		
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT department.* FROM department ORDER BY Name ");
			rs = st.executeQuery();
			List<Department> lista = new ArrayList<>();
			if(rs.next()) {
				while(rs.next()) {
					Department dp = new Department();
					dp.setId(rs.getInt("Id"));
					dp.setNome(rs.getString("Name"));
					lista.add(dp);
				}
				return lista;				
			}else {
				throw new DbException("Lista de Departamento n?o criada!");
			}
		
		}catch(SQLException e) {
			throw new DbException("Erro: "+e.getMessage());
		}finally {
			DB.closeResultset(rs);
			DB.closeStatement(st);
		}
		
	}

}
