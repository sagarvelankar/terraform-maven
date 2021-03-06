package com.deliveredtechnologies.maven.terraform.mojo;

import com.deliveredtechnologies.terraform.TerraformException;
import com.deliveredtechnologies.terraform.api.TerraformClean;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Mojo clean goal.
 * <br>
 * Deletes all terraform files from terraform configurations along with terraform modules directory.
 */
@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN)
public class Clean extends TerraformMojo<String> {

  @Parameter(property = "tfRootDir")
  String tfRootDir;

  @Parameter(property = "tfModulesDir")
  String tfModulesDir;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      execute(new TerraformClean(tfModulesDir, tfRootDir), getFieldsAsProperties());
    } catch (IOException | TerraformException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }
}
