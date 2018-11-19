package com.template.webserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.template.CreateBond;
import com.template.UKTFBondState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/uktf") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final CordaX500Name myLegalName;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    private final List<String> serviceNames = ImmutableList.of("Notary");
    private  Party exporter;
    private  Party bank;
    private  Party ukef;

    public Controller(NodeRPCConnection rpc) {

        this.proxy = rpc.proxy;
        this.myLegalName = proxy.nodeInfo().getLegalIdentities().get(0).getName();

        this.exporter = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Exporter,L=Southampton,C=GB"));
        this.bank = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Bank,L=London,C=GB"));
        this.ukef = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse("O=UKEF,L=London,C=GB"));

    }

    /**
     * Return the name of the node
     */
    @RequestMapping(value = "/me", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, CordaX500Name> whoami(){
        return ImmutableMap.of("me", myLegalName);
    }


    /**
     * Returns all the registered peers
     */
    @RequestMapping(value = "/peers", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<CordaX500Name>> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = proxy.networkMapSnapshot();
        return ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));
    }

    /**
     * Register a new bond
     */
    @RequestMapping(value = "/createBond", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONObject createBond(@RequestParam("bondId") String bondId,
                                 @RequestParam("bondValue") int bondValue,
                                 @RequestParam("bondUKValue") int bondUKValue) {

        JSONObject json = new JSONObject();

        if (bondValue <= 0) {
            json.put("err", "Query parameter 'bondValue' must be non-negative");
            return json;
        }

        try {
            FlowHandle<SignedTransaction> flow_bond = proxy.startFlowDynamic(
                    CreateBond.Initiator.class,
                    bondId, bondValue, bondUKValue, bank, ukef
            );

            SignedTransaction trx = flow_bond.getReturnValue().get();

            logger.info(trx.toString());

            json.put("trxId" , trx.getId().toString());
            return json;

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            json.put("err", msg);
            return json;
        }
    }

//
//    /**
//     * Register bank assess
//     */
//    @RequestMapping(value = "/createBond", method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public JSONObject createBond(@RequestParam("bondId") String bondId,
//                                 @RequestParam("bondValue") int bondValue) {
//
//
//    }


    /**
     * Returns all the registered peers
     */
    @RequestMapping(value = "/bonds", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONObject getBonds() {

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        Iterator<StateAndRef<UKTFBondState>> i = getUKTFBonds().iterator();
        int j = 0;
        while (i.hasNext()) {
            JSONObject item = new JSONObject();
            UKTFBondState bond = i.next().getState().getData();
            item.put("n", j);

            //exporter
            item.put("id", bond.getBondID());
            item.put("bondValue", bond.getBondValue());
            item.put("bondUKValue", bond.getBondUKValue());
            //bank
            item.put("bankId", bond.getBankSupplyContract());
            item.put("turnover", bond.getTurnover());
            item.put("net", bond.getNet());
            item.put("defaultProb", bond.getDefaultProbability());
            item.put("creditRate", bond.getCreditRating());
            item.put("requestedSupport", bond.getRequestedUKEFsupport());
            //ukef
            item.put("ukefId", bond.getUkefSupplyContract());
            item.put("ukefSupport", bond.getUKEFSupported());
            array.add(item);

            j++;
        }


        json.put("bonds", array);

        logger.info("result values" +  json.toJSONString());

        return json;

    }

    private List<StateAndRef<UKTFBondState>> getUKTFBonds () {

        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Vault.Page<UKTFBondState> results = proxy.vaultQueryByCriteria(criteria, UKTFBondState.class);
        List<StateAndRef<UKTFBondState>> bonds = results.getStates();

        logger.info("Number of UNCONSUMED bonds " +  bonds.toArray().length);

        return bonds;
    }





}