 <?xml version='1.0' encoding='ISO-8859-1' ?>
    <!DOCTYPE helpset
      PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
             "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

    <!-- next is a Processing Instruction (PI).
      This is ignored by the Reference Implementation -->
    <?MyFavoriteApplication this is data for my favorite application ?>

    <helpset version="1.0">

        <!-- Global properties -->
        <title>ISYS</title>

        <!-- maps section -->
        <maps>
            <homeID>isys-overview</homeID>
            <mapref location="map.jhm"/>
        </maps>

        <!-- Zero or more View sections -->
        <view>
            <name>TOC</name>
            <label>ISYS</label>
            <type>javax.help.TOCView</type>
            <data>toc.xml</data>
        </view>

    </helpset>