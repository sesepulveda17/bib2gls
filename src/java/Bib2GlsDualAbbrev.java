/*
    Copyright (C) 2017 Nicola L.C. Talbot
    www.dickimaw-books.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.dickimawbooks.bib2gls;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.text.CollationKey;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.bib.*;
import com.dickimawbooks.texparserlib.latex.CsvList;

public class Bib2GlsDualAbbrev extends Bib2GlsDualEntry
{
   public Bib2GlsDualAbbrev(Bib2Gls bib2gls)
   {
      this(bib2gls, "dualabbreviation");
   }

   public Bib2GlsDualAbbrev(Bib2Gls bib2gls, String entryType)
   {
      super(bib2gls, entryType);
   }

   public HashMap<String,String> getMappings()
   {
      return getResource().getDualAbbrevMap();
   }

   public String getFirstMap()
   {
      return getResource().getFirstDualAbbrevMap();
   }

   public boolean backLink()
   {
      return getResource().backLinkFirstDualAbbrevMap();
   }

   protected Bib2GlsEntry createDualEntry()
   {
      return new Bib2GlsDualAbbrev(bib2gls, getEntryType());
   }

   public void checkRequiredFields(TeXParser parser)
   {
      if (getField("short") == null)
      {
         missingFieldWarning(parser, "short");
      }

      if (getField("long") == null)
      {
         missingFieldWarning(parser, "long");
      }

      if (getField("dualshort") == null)
      {
         missingFieldWarning(parser, "dualshort");
      }

      if (getField("duallong") == null)
      {
         missingFieldWarning(parser, "duallong");
      }
   }

   public String getFallbackValue(String field)
   {
      String val;

      if (field.equals("name"))
      {
         val = getFieldValue("short");

         if (val != null) return val;
      }

      val = super.getFallbackValue(field);

      if (val != null) return val;

      if (field.equals("sort"))
      {
         return getFieldValue("short");
      }

      return null;
   }

   public BibValueList getFallbackContents(String field)
   {
      BibValueList val;

      if (field.equals("sort"))
      {
         val = getField("name");

         return val == null ? getFallbackContents("name") : val;
      }
      else if (field.equals("name"))
      {
         val = getField("short");

         if (val != null) return val;
      }

      return super.getFallbackContents(field);
   }

   public void writeBibEntry(PrintWriter writer)
   throws IOException
   {
      writer.format("\\%s{%s}%%%n{", getCsName(), getId());

      String sep = "";
      String shortText = "";
      String longText = "";

      Set<String> keyset = getFieldSet();

      Iterator<String> it = keyset.iterator();

      while (it.hasNext())
      {
         String field = it.next();

         if (field.equals("short"))
         {
            shortText = getFieldValue(field);
         }
         else if (field.equals("long"))
         {
            longText = getFieldValue(field);
         }
         else
         {
            writer.format("%s", sep);

            sep = String.format(",%n");

            writer.format("%s={%s}", field, getFieldValue(field));
         }
      }

      writer.println(String.format("}%%%n{%s}%%%n{%s}",
        shortText, longText));
   }

   public void writeCsDefinition(PrintWriter writer) throws IOException
   {
      // syntax: {label}{opts}{short}{long}

      writer.println("\\glsxtrprovidestoragekey{dualshort}{}{}");
      writer.println("\\glsxtrprovidestoragekey{dualshortplural}{}{}");
      writer.println("\\glsxtrprovidestoragekey{duallong}{}{}");
      writer.println("\\glsxtrprovidestoragekey{duallongplural}{}{}");

      writer.format("\\providecommand{\\%s}[4]{%%%n", getCsName());

      String newcs = getEntryType();

      if (newcs.startsWith("dual"))
      {
         newcs = newcs.substring(4);
      }

      writer.format("  \\new%s[#2]{#1}{#3}{#4}%%%n", newcs);

      writer.println("}");
   }
}