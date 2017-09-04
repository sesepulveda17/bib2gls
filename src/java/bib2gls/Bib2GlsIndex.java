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
import java.util.Set;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.bib.*;

public class Bib2GlsIndex extends Bib2GlsEntry
{
   public Bib2GlsIndex(Bib2Gls bib2gls)
   {
      this(bib2gls, "index");
   }

   public Bib2GlsIndex(Bib2Gls bib2gls, String entryType)
   {
      super(bib2gls, entryType);
   }

   // initialise the name field if a label prefix is supplied

   public void parseContents(TeXParser parser,
    TeXObjectList contents, TeXObject endGroupChar)
     throws IOException
   {
      super.parseContents(parser, contents, endGroupChar);

      if (getResource().getLabelPrefix() != null
         && getFieldValue("name") == null)
      {
         putField("name", getOriginalId());
      }
   }

   public void checkRequiredFields(TeXParser parser)
   {// no required fields
   }

   public String getFallbackValue(String field)
   {
      if (field.equals("name"))
      {
         return getOriginalId();
      }
      else
      {
         return super.getFallbackValue(field);
      }
   }

   public BibValueList getFallbackContents(String field)
   {
      if (field.equals("name") && bib2gls.useInterpreter())
      {
         String name = getOriginalId();
         BibValueList list = new BibValueList();
         list.add(new BibUserString(
            bib2gls.getInterpreterListener().createGroup(name)));

         return list;
      }
      else
      {
         return super.getFallbackContents(field);
      }
   }

   public void writeBibEntry(PrintWriter writer)
   throws IOException
   {
      writer.format("\\%s{%s}%%%n{", getCsName(), getId());

      String sep = "";

      Set<String> keyset = getFieldSet();

      Iterator<String> it = keyset.iterator();

      while (it.hasNext())
      {
         String field = it.next();

         writer.format("%s", sep);

         sep = String.format(",%n");
         writer.format("%s={%s}", field, getFieldValue(field));
      }

      writer.println("}");
   }

   public void writeCsDefinition(PrintWriter writer) throws IOException
   {
      // syntax: {label}{opts}

      writer.format("\\providecommand{\\%s}[2]{%%%n", getCsName());

      writer.println(" \\newglossaryentry{#1}{name={#1},description={},#2}%");

      writer.println("}");
   }
}
