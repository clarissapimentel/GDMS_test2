/**
 * 
 */
package org.icrisat.gdms.upload;

public class DatasetBean {
	
	private int dataset_id;
	private String dataset_desc;
	private String dataset_type;
	private String template_date;
	
	private String genus;
	private String species;
	private String datatype;
	private String remarks;
	public int getDataset_id() {
		return dataset_id;
	}
	public void setDataset_id(int dataset_id) {
		this.dataset_id = dataset_id;
	}
	public String getDataset_desc() {
		return dataset_desc;
	}
	public void setDataset_desc(String dataset_desc) {
		this.dataset_desc = dataset_desc;
	}
	public String getDataset_type() {
		return dataset_type;
	}
	public void setDataset_type(String dataset_type) {
		this.dataset_type = dataset_type;
	}
	public String getTemplate_date() {
		return template_date;
	}
	public void setTemplate_date(String template_date) {
		this.template_date = template_date;
	}
	public String getGenus() {
		return genus;
	}
	public void setGenus(String genus) {
		this.genus = genus;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	

}
