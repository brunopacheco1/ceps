package com.dev.bruno.ceps.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.dev.bruno.ceps.model.AbstractModel;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultList<ENTITY extends AbstractModel> implements Serializable {

	private static final long serialVersionUID = -4216175342452534456L;

	private List<ENTITY> result = new ArrayList<>();

	private Integer size = 0;

	private Integer start = 0;

	private Integer limit = 100;

	private String order = "id";

	private String dir = "asc";

	public List<ENTITY> getResult() {
		return result;
	}

	public void setResult(List<ENTITY> result) {
		this.result = result;
		this.size = result.size();
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
}