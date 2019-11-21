package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="ingredientes")
public class Ingrediente implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false )
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Alimento alimento;
	
	private double  cantidad;
	
	
	private double proteinastotales;
	private double grasastotales;
	private double kcaltotales;
	private double hidratostotales;
	
	
	
	
	
	
	public Ingrediente() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Alimento getAlimento() {
		return alimento;
	}

	public void setAlimento(Alimento alimento) {
		this.alimento = alimento;
	}

	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	
	

	
	
	@Override
	public String toString() {
		return "Ingrediente [id=" + id + ", alimento=" + alimento + ", cantidad=" + cantidad + ", proteinastotales="
				+ proteinastotales + ", grasastotales=" + grasastotales + ", kcaltotales=" + kcaltotales
				+ ", hidratostotales=" + hidratostotales + "]";
	}

	public double getProteinastotales() {
		return proteinastotales;
	}
	public void setProteinastotales(double proteinastotales) {
		this.proteinastotales = proteinastotales;
	}
	public double getGrasastotales() {
		return grasastotales;
	}
	public void setGrasastotales(double grasastotales) {
		this.grasastotales = grasastotales;
	}
	public double getKcaltotales() {
		return kcaltotales;
	}
	public void setKcaltotales(double kcaltotales) {
		this.kcaltotales = kcaltotales;
	}
	public double getHidratostotales() {
		return hidratostotales;
	}
	public void setHidratostotales(double hidratostotales) {
		this.hidratostotales = hidratostotales;
	}
	
	/*

 
    @EmbeddedId
    private PostTagId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    private Post post;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;
 
    @Column(name = "created_on")
    private Date createdOn = new Date();
 
    private PostTag() {}
 
    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        this.id = new PostTagId(post.getId(), tag.getId());
    }
 
    //Getters and setters omitted for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        PostTag that = (PostTag) o;
        return Objects.equals(post, that.post) &&
               Objects.equals(tag, that.tag);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(post, tag);
    }
	 * 
	 */
	
}
