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

	private Long resultSize = 0l;

	private Long totalSize = 0l;

	private Integer start;

	private Integer limit;

	private String order;

	private String dir;

	public void remove(ENTITY dto) {
		result.remove(dto);

		if (resultSize > 0) {
			resultSize--;
		}

		if (totalSize > 0) {
			totalSize--;
		}
	}

	public void add(ENTITY dto) {
		result.add(dto);

		resultSize++;

		totalSize++;
	}

	public List<ENTITY> getResult() {
		return result;
	}

	public void setResult(List<ENTITY> result) {
		this.result = result;
	}

	public Long getResultSize() {
		return resultSize;
	}

	public void setResultSize(Long resultSize) {
		this.resultSize = resultSize;
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
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