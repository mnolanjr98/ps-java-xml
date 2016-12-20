<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns="http://www.mypartner.com/customer"
				xmlns:tbc="http://www.thoughtbend.com/customer/v1">
	<xsl:output method="xml" />
	<xsl:template match="/">
		<PartnerCustomers>
			<xsl:for-each select="tbc:customers/tbc:customer">
				<Customer>
					<CustomerId><xsl:value-of select="tbc:id" /></CustomerId>
					<FirstName><xsl:value-of select="tbc:firstName" /></FirstName>
					<LastName><xsl:value-of select="tbc:lastName" /></LastName>
					<PersonalEmail><xsl:value-of select="tbc:email" /></PersonalEmail>
				</Customer>
			</xsl:for-each>
		</PartnerCustomers>
	</xsl:template>
	
</xsl:stylesheet>