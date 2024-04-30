package org.test.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.test.gitb.StateManager;
import org.test.state.SutMessage;

/**
 * Implementation of the Purchase Order REST API to receive messages from SUTs.
 */
@RestController
public class PurchaseOrderServer {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderServer.class);

    @Autowired
    private StateManager stateManager = null;

    @ResponseBody
    @PostMapping(path = "/api/{vatNumber}/receiveOrder", produces = MediaType.TEXT_PLAIN_VALUE)
    public String receiveOrder(@PathVariable("vatNumber") String vatNumber, @RequestBody String content) {
        LOG.info("Received call for VAT number [{}]", vatNumber);
        stateManager.handleSutMessage(new SutMessage(vatNumber, content));
        return "REF-0123456789";
    }

}
