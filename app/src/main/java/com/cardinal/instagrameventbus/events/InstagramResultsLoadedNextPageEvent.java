package com.cardinal.instagrameventbus.events;

import com.cardinal.instagrameventbus.controller.GetInstagramResult;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         10:00
 */
public class InstagramResultsLoadedNextPageEvent extends InstagramResultsLoadedEvent {

    public InstagramResultsLoadedNextPageEvent(boolean successIn, GetInstagramResult instagramResultsIn) {
        super(successIn, instagramResultsIn);
    }

}
