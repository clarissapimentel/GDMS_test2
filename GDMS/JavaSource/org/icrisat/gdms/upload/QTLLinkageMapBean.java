package org.icrisat.gdms.upload;

public class QTLLinkageMapBean {
	private int linkagemap_id;
	private int qtl_id;
	private String linkage_group;
	private float min_position;
	private float max_position;
	private String trait;
	private String experiment;
	private String left_flanking_marker;
	private String right_flanking_marker;
	private float effect;
	private float lod;
	private float r_square;
	private String interactions;
	public int getLinkagemap_id() {
		return linkagemap_id;
	}
	public void setLinkagemap_id(int linkagemap_id) {
		this.linkagemap_id = linkagemap_id;
	}
	public int getQtl_id() {
		return qtl_id;
	}
	public void setQtl_id(int qtl_id) {
		this.qtl_id = qtl_id;
	}
	
		
	
	public String getLinkage_group() {
		return linkage_group;
	}
	public void setLinkage_group(String linkage_group) {
		this.linkage_group = linkage_group;
	}
	public float getMin_position() {
		return min_position;
	}
	public void setMin_position(float min_position) {
		this.min_position = min_position;
	}
	public float getMax_position() {
		return max_position;
	}
	public void setMax_position(float max_position) {
		this.max_position = max_position;
	}
	public String getTrait() {
		return trait;
	}
	public void setTrait(String trait) {
		this.trait = trait;
	}
	public String getExperiment() {
		return experiment;
	}
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}
	public String getLeft_flanking_marker() {
		return left_flanking_marker;
	}
	public void setLeft_flanking_marker(String left_flanking_marker) {
		this.left_flanking_marker = left_flanking_marker;
	}
	public String getRight_flanking_marker() {
		return right_flanking_marker;
	}
	public void setRight_flanking_marker(String right_flanking_marker) {
		this.right_flanking_marker = right_flanking_marker;
	}
	
	public float getEffect() {
		return effect;
	}
	public void setEffect(float effect) {
		this.effect = effect;
	}
	public float getLod() {
		return lod;
	}
	public void setLod(float lod) {
		this.lod = lod;
	}
	public float getR_square() {
		return r_square;
	}
	public void setR_square(float r_square) {
		this.r_square = r_square;
	}
	public String getInteractions() {
		return interactions;
	}
	public void setInteractions(String interactions) {
		this.interactions = interactions;
	}
	
	
}
