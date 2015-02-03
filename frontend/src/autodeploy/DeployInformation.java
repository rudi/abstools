package autodeploy;

import abs.frontend.ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class DeployInformation {

  private Map<String, DeployInformationClass> _map;
  private Map<String, Set<String>> _hierarchy;

  public DeployInformation() {
    _map = new HashMap<String, DeployInformationClass>();
    _hierarchy = new HashMap<String, Set<String>>();
  }

  public void extractInformation(Model model) {
    extractHierarchy(model);
    extractDeployInformationClasses(model);
  }

  private void extractHierarchy(Model model) {
    for (Decl decl : model.getDecls()) {
      abs.frontend.ast.List<InterfaceTypeUse> list = null;
      if (decl instanceof InterfaceDecl) { list =  ((InterfaceDecl) decl).getExtendedInterfaceUseList(); }
      if (decl instanceof ClassDecl) { list =  ((ClassDecl) decl).getImplementedInterfaceUseList(); }
      if(list != null) {
        Set<String> extended = new HashSet<String>();
        for (InterfaceTypeUse use : list) {
          extended.add(use.getType().getQualifiedName());
        }
        _hierarchy.put(decl.getType().getQualifiedName(), extended);
      }
    }
  }

  private void extractDeployInformationClasses(Model model) {
    int i = 0;
    boolean toAdd;
    for (Decl decl : model.getDecls()) {
      if (decl instanceof ClassDecl) {
        toAdd = false;
        DeployInformationClass dic = new DeployInformationClass(((ClassDecl) decl).getParamList());
        for(Annotation ann: ((ClassDecl) decl).getAnnotationList()) {
          System.out.println(i++);
          if(ann.getType().getSimpleName().equals("Aeolus")) { toAdd = true; dic.addAnn(ann.getValue()); }
        }
        if(toAdd) _map.put(decl.getType().getQualifiedName(), dic);
      }
    }
  }

  public void generateJSON(PrintWriter f) {
    f.write("{\n");
    f.write("  \"classes\": [\n");
    Iterator<Map.Entry<String, DeployInformationClass>> iClass = _map.entrySet().iterator();
    while(iClass.hasNext()) {
      Map.Entry<String, DeployInformationClass> entry = iClass.next();
      System.out.println("Generating JSON for the class \"" + entry.getKey() + "\"");
      entry.getValue().generateJSON(entry.getKey(), f);
      if(iClass.hasNext()) f.write(",\n");
    }
    f.write("  ],\n");
    f.write("  \"hierarchy\": {\n");
    Iterator<String> iClassName = _map.keySet().iterator();
    while(iClassName.hasNext()) {
      String className = iClassName.next();
      f.write("    \"" + className + "\": [");
      Set<String> implemented =  _hierarchy.get(className);
      if ((implemented != null) && (!implemented.isEmpty())) { generateImplemented(implemented, f); }
      f.write("]");
      if (iClassName.hasNext()) { f.write(",\n"); }
      else { f.write("\n  }\n"); }
    }
    f.write("}");
  }

  private void generateImplemented(Set<String> names, PrintWriter f) {
    Iterator<String> iName = names.iterator();
    while(iName.hasNext()) {
      String name = iName.next();
      f.write("\"" + name + "\"");
      Set<String> extended = _hierarchy.get(name);
      if ((extended != null) && (!extended.isEmpty())) {
        f.write(", ");
        generateImplemented(extended, f);
      }
      if (iName.hasNext()) { f.write(", "); }
    }
  }
}
