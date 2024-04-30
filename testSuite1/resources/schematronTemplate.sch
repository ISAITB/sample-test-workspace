<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">
    <title>Purchase Order rules</title>
    <ns prefix="po" uri="http://itb.ec.europa.eu/sample/po.xsd"/>
    <pattern name="General checks">
        <rule context="/po:purchaseOrder">
            <assert test="string(@orderDate) = '${expectedOrderDate}'" flag="fatal" id="PO-01">The order date must match today's date (${expectedOrderDate}).</assert>
        </rule>
    </pattern>	
</schema>  