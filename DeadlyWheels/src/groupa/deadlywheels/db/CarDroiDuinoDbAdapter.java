package groupa.deadlywheels.db;

import groupa.deadlywheels.utils.SystemProperties;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Database access helper Class. Classe que implementa o
 * acesso ao SQL Lite do Android para gravar dados em seu 
 * pequeno Banco de Dados interno 
 * 
 * 
 * <p>
 * <b>Data da Criacao:</b> 30/04/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class CarDroiDuinoDbAdapter {
	
	/**
	 * Classe auxiliadora para cria��o de DataBases
	 */
	private DatabaseHelper databaseHelper;
	
	/**
	 * Classe para gerenciamento de SQLLite databases
	 */
    private SQLiteDatabase sqlLiteDatabase;
    
    /**
     * Interface para acesso ao ambiente da aplica��o
     */
    private Context context;

    /**
     * Classe auxiliadora para gerenciar a cria��o de Bancos de Dados SQLLite
     * Herda de SQLiteOpenHelper (gerenciador padr�o)
     * 
     * @author Leandro.Piqueira
     * 
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

    	/**
    	 * Cria uma nova inst�ncia da classe auxiliadora e
    	 * tambem a base de dados
    	 * @param context
    	 */
        public DatabaseHelper(Context context) {
           super(context, SystemProperties.DATABASE_NAME, null,SystemProperties.DATABASE_VERSION);
        }
        
        /**
         * Executa a cria��o da tabela de propriedades
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SystemProperties.TABLE_CREATE);
        }
        
        /**
         * Caso ocorrar um upgrade da vers�o do banco de dados, deleta a tabela antiga
         * (ser� necess�rio reinserir as propriedades
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SystemProperties.TABLE_DROP);
            onCreate(db);
        }
    }
    
    /**
     * Cria uma nova inst�ncia do Adaptador da Database
     * @param context 
     */
    public CarDroiDuinoDbAdapter(Context context){
    	this.context = context;
    }
    
    /**
     * Cria/Abre a database de propriedaes do Sistema
     * @return Classe de gerenciamento da conex�o
     * @throws SQLException
     */
    public CarDroiDuinoDbAdapter open() throws SQLException {
        this.databaseHelper = new DatabaseHelper(this.context);
        this.sqlLiteDatabase = this.databaseHelper.getWritableDatabase();
        return this;
    }
    
    /**
     * Fecha a conex�o com a database do sistema
     */
    public void close() {
    	this.databaseHelper.close();
    }
    
    /**
     * M�todo para inserir uma propriedade no Banco de Dados
     * @param _id
     * @param propriedade
     * @param descricao
     * @return Row id do registro ou -1 se houver falha
     */
    public long createPropertie(int _id, String propriedade, String descricao){
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(SystemProperties.DATABASE_FIELD_ID, _id);
    	initialValues.put(SystemProperties.DATABASE_FIELD_PROP, propriedade);
    	initialValues.put(SystemProperties.DATABASE_FIELD_DESC, descricao);
    	
    	return this.sqlLiteDatabase.insert(SystemProperties.DATABASE_TABLE, null, initialValues);    			
    }
    
    /**
     * M�todo para efetuar o update de uma propriedade
     * @param _id
     * @param propriedade
     * @return True --> Alterado | False --> N�o Alterado
     */
    public boolean updatePropertie(int _id, String propriedade){
    	ContentValues values = new ContentValues();
    	values.put(SystemProperties.DATABASE_FIELD_PROP, propriedade);
    	
    	return this.sqlLiteDatabase.update(SystemProperties.DATABASE_TABLE, values, SystemProperties.DATABASE_FIELD_ID + "=" + _id, null) > 0;
    }
    
    /**
     * M�todo para deletar uma propriedade
     * @param _id
     * @return True --> Deletado | False --> N�o Deletado 
     */
    public boolean deletePropertie(int _id){
    	return this.sqlLiteDatabase.delete(SystemProperties.DATABASE_TABLE, SystemProperties.DATABASE_FIELD_ID + "=" + _id, null) > 0;
    }
    
    /**
     * M�todo para selecionar uma propriedade
     * @param _id
     * @return Cursor com o registro requisitado
     */
    public Cursor fetchPropertie(int _id){
    	Cursor propCursor = this.sqlLiteDatabase.query(true, 
    													SystemProperties.DATABASE_TABLE, 
    													new String[] {SystemProperties.DATABASE_FIELD_ID, 
    																	SystemProperties.DATABASE_FIELD_PROP,
    																	SystemProperties.DATABASE_FIELD_DESC}, 
    																	SystemProperties.DATABASE_FIELD_ID + "=" + _id, 
    													null, null, null, null, null);
    	if (propCursor != null) {
    		propCursor.moveToFirst();
        }
        return propCursor;
    }
}
