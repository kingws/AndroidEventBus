package com.cardinal.instagrameventbus.controller;

import com.cardinal.instagrameventbus.model.Instagram;
import com.cardinal.instagrameventbus.model.Meta;
import com.cardinal.instagrameventbus.model.Pagination;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         08:58
 */
public class GetInstagramResult {
	@Expose
	private Pagination pagination;
	@Expose
	private Meta meta;
	@Expose
	private List<Instagram> data = new ArrayList<Instagram>();

	/**
	 *
	 * @return
	 *     The pagination
	 */
	public Pagination getPagination() {
		return pagination;
	}

	/**
	 *
	 * @param pagination
	 *     The pagination
	 */
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	/**
	 *
	 * @return
	 *     The meta
	 */
	public Meta getMeta() {
		return meta;
	}

	/**
	 *
	 * @param meta
	 *     The meta
	 */
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	/**
	 *
	 * @return
	 *     The data
	 */
	public List<Instagram> getData() {
		return data;
	}

	/**
	 *
	 * @param data
	 *     The data
	 */
	public void setData(List<Instagram> data) {
		this.data = data;
	}

}
