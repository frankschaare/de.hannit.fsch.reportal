package de.hannit.fsch.reportal.model;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.db.Cache;

@ManagedBean
@RequestScoped
public class DatumsBaumHannIT implements Serializable
{
private static final long serialVersionUID = 3134101468260235420L;
@ManagedProperty (value = "#{cache}")
private Cache cache;


	public DatumsBaumHannIT() 
	{
	}

	public Cache getCache() 
	{
	return cache;
	}

	public void setCache(Cache cache) 
	{
	this.cache = cache;
	}

    public TreeNode getRoot() 
    {
    cache.setMandant("HannIT");	
    return cache.getRoot();	
    } 
}
