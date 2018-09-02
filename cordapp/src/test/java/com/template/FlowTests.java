package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.ExecutionException;

import static net.corda.testing.internal.InternalTestUtilsKt.chooseIdentity;

public class FlowTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private MockNetwork network;
    private StartedMockNode exp, bank, ukef;

    @Before
    public void setup() {
        network = new MockNetwork(ImmutableList.of("com.template"));
        exp = network.createNode();
        bank = network.createNode();
        ukef = network.createNode();

        for (StartedMockNode node : ImmutableList.of(exp, bank, ukef)) {
            node.registerInitiatedFlow(CreateBond.Initiator.class);
            node.registerInitiatedFlow(CreateBond.UKTFBankFlow.class);
        }

        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void issueBond() throws Exception {
        //Party expP = chooseIdentity(exp.getInfo());
        Party bankP = chooseIdentity(bank.getInfo());
        Party ukefP = chooseIdentity(ukef.getInfo());
        CreateBond.Initiator flow = new CreateBond.Initiator("b1", 500, bankP, ukefP);
        exp.startFlow(flow).get();

        network.runNetwork();
    }


}
