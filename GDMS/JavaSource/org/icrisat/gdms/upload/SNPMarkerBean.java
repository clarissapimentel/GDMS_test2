package org.icrisat.gdms.upload;

public class SNPMarkerBean {
	private int marker_id;
	private String assay_type;
	private String forward_primer;
	private String reverse_primer;
	private int product_size;
	private int expected_product_size;
	private int position_on_reference_sequence;
	private String motif;
	private double annealing_temp;
	private String sequence;
	public int getMarker_id() {
		return marker_id;
	}
	public void setMarker_id(int marker_id) {
		this.marker_id = marker_id;
	}
	public String getAssay_type() {
		return assay_type;
	}
	public void setAssay_type(String assay_type) {
		this.assay_type = assay_type;
	}
	public String getForward_primer() {
		return forward_primer;
	}
	public void setForward_primer(String forward_primer) {
		this.forward_primer = forward_primer;
	}
	public String getReverse_primer() {
		return reverse_primer;
	}
	public void setReverse_primer(String reverse_primer) {
		this.reverse_primer = reverse_primer;
	}
	public int getProduct_size() {
		return product_size;
	}
	public void setProduct_size(int product_size) {
		this.product_size = product_size;
	}
	public int getExpected_product_size() {
		return expected_product_size;
	}
	public void setExpected_product_size(int expected_product_size) {
		this.expected_product_size = expected_product_size;
	}
	public int getPosition_on_reference_sequence() {
		return position_on_reference_sequence;
	}
	public void setPosition_on_reference_sequence(int position_on_reference_sequence) {
		this.position_on_reference_sequence = position_on_reference_sequence;
	}
	public String getMotif() {
		return motif;
	}
	public void setMotif(String motif) {
		this.motif = motif;
	}
	public double getAnnealing_temp() {
		return annealing_temp;
	}
	public void setAnnealing_temp(double annealing_temp) {
		this.annealing_temp = annealing_temp;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	
	
}
