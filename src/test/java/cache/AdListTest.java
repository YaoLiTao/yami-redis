package cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdListTest {

    private AdList<String> adList = new AdList<>();

    @Test
    void listAddNodeHead() {
        adList.listAddNodeHead("4");
        adList.listAddNodeHead("3");
        adList.listAddNodeHead("2");
        adList.listAddNodeHead("1");
        Assertions.assertEquals(4, adList.getLen());
    }

    @Test
    void listAddNodeTail() {
        listAddNodeHead();
        adList.listAddNodeTail("5");
        adList.listAddNodeTail("6");
        adList.listAddNodeTail("7");
        adList.listAddNodeTail("8");
        Assertions.assertEquals(8, adList.getLen());
    }

    @Test
    void listInsertNode() {
        listAddNodeTail();
        ListNode<String> oldNode = adList.listSearchKey("7");
        adList.listInsertNode(oldNode, "7.5", true);
        Assertions.assertEquals(9, adList.getLen());
        adList.listInsertNode(oldNode, "6.5", false);
        Assertions.assertEquals(10, adList.getLen());
    }

    @Test
    void listSearchKey() {
        listInsertNode();
        ListNode<String> node = adList.listSearchKey("7");
        Assertions.assertEquals(node.value, "7");
    }

    @Test
    void listIndex() {
        listInsertNode();
        ListNode<String> node = adList.listIndex(7);
        Assertions.assertEquals(node.value, "7");
    }

    @Test
    void listDelNode() {
        listInsertNode();
        ListNode<String> node = adList.listIndex(7);
        adList.listDelNode(node);
        Assertions.assertEquals(adList.getLen(), 9);
        node = adList.listIndex(7);
        Assertions.assertEquals(node.value, "7.5");
    }
}