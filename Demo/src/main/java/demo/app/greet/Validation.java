import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

public class XPathUtils {

    // Thread-safe factories
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    // Thread-local DocumentBuilder (since DocumentBuilder is NOT thread-safe)
    private static final ThreadLocal<DocumentBuilder> DOCUMENT_BUILDER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DocumentBuilder", e);
        }
    });

    // Thread-local XPath (XPath is NOT thread-safe)
    private static final ThreadLocal<XPath> XPATH_THREAD_LOCAL = ThreadLocal.withInitial(XPATH_FACTORY::newXPath);

    /**
     * Parses XML string into a Document (Thread-safe using ThreadLocal DocumentBuilder)
     */
    public static Document parseXml(String xml) {
        try {
            return DOCUMENT_BUILDER_THREAD_LOCAL.get()
                    .parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML", e);
        }
    }

    /**
     * Evaluates an XPath expression on the given XML document.
     */
    public static boolean isMessagePresent(Document document, String description) {
        try {
            String expression = String.format("count(//Message[Description='%s']) > 0", description);
            XPathExpression xpathExpr = XPATH_THREAD_LOCAL.get().compile(expression);
            return (Boolean) xpathExpr.evaluate(document, XPathConstants.BOOLEAN);
        } catch (Exception e) {
            throw new RuntimeException("XPath evaluation failed", e);
        }
    }

    public static void main(String[] args) {
        String xml = """
                <Messages>
                    <Message>
                        <ID>1</ID>
                        <Description>Order received</Description>
                    </Message>
                    <Message>
                        <ID>2</ID>
                        <Description>Processing</Description>
                    </Message>
                    <Message>
                        <ID>3</ID>
                        <Description>Shipped</Description>
                    </Message>
                </Messages>
                """;

        // Parse XML once and reuse
        Document document = XPathUtils.parseXml(xml);

        // Check for message presence in a thread-safe way
        System.out.println("Message 'Shipped' present? " + XPathUtils.isMessagePresent(document, "Shipped"));
        System.out.println("Message 'Delivered' present? " + XPathUtils.isMessagePresent(document, "Delivered"));
    }
}